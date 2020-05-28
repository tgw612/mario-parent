package com.mario.es.service.impl;

import org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.alibaba.fastjson.JSONObject;
import com.mario.es.exception.CustomSearchException;
import com.mario.es.model.EsQuery;
import com.mario.es.model.EsResultData;
import com.mario.es.service.EsSearchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

@Slf4j
public class EsSearchServiceImpl implements EsSearchService {

  private final static String SEARCH_PARAMS_ERR = "搜索参数异常";

  @Setter
  private TransportClient client;

  @Override
  public void add(String indexName, String tableName, final String id, JSONObject json)
      throws CustomSearchException {
    if (null == json || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    try {
      if (!json.containsKey("id")) {
        // 生成id
        json.put("id", id);
      }
      CompletableFuture.runAsync(() -> {
        client.prepareIndex(indexName, tableName, id).setSource(json).execute().actionGet();
      });
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_ADD_ERROR);
      throw new CustomSearchException();
    }
  }

  @Override
  public void upsert(String indexName, String tableName, String id, JSONObject json)
      throws CustomSearchException {
    if (null == json || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    try {
      if (StringUtils.isBlank(id)) {
        // 生成id
        id = UUID.randomUUID().toString().replace("-", "");
      }

      XContentBuilder indexXBuilder = jsonBuilder().startObject();
      for (String key : json.keySet()) {
        indexXBuilder.field(key, json.get(key));
      }
      indexXBuilder.endObject();
      IndexRequest indexRequest = new IndexRequest(indexName, tableName, id).source(indexXBuilder);

      XContentBuilder updateXBuilder = jsonBuilder().startObject();
      for (String key : json.keySet()) {
        updateXBuilder.field(key, json.get(key));
      }
      updateXBuilder.endObject();
      UpdateRequest updateRequest = new UpdateRequest(indexName, tableName, id).doc(updateXBuilder)
          .upsert
              (indexRequest);
      client.update(updateRequest).actionGet();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_ADD_ERROR);
      throw new CustomSearchException();
    }
  }

  @Override
  public void updateById(String indexName, String tableName, String id, JSONObject json)
      throws CustomSearchException {
    if (null == json || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)
        || StringUtils.isBlank
        (id)) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    try {
      XContentBuilder xBuilder = jsonBuilder().startObject();
      for (String key : json.keySet()) {
        xBuilder.field(key, json.get(key));
      }
      xBuilder.endObject();
      UpdateRequest updateRequest = new UpdateRequest(indexName, tableName, id).doc(xBuilder);
      client.update(updateRequest).actionGet();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_UPDATE_ERROR);
      throw new CustomSearchException();
    }
  }

  @Override
  public void deleteById(String indexName, String tableName, String id)
      throws CustomSearchException {
    try {
      client.prepareDelete(indexName, tableName, id).execute().actionGet();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_DEL_ERROR);
      throw new CustomSearchException();
    }
  }

  @Override
  public JSONObject getById(String indexName, String tableName, String id)
      throws CustomSearchException {
    if (StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName) || StringUtils
        .isBlank(id)) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    try {
      GetResponse response = client.prepareGet(indexName, tableName, id).execute().actionGet();
      if (null != response.getSourceAsString()) {
        return JSONObject.parseObject(response.getSourceAsString());
      }
      return null;
    } catch (Exception e) {
      if ("no such index".equals(e.getMessage())) {
        return null;
      }
      e.printStackTrace();
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_QUERY_ERROR);
      throw new CustomSearchException();
    }
  }

  @Override
  public EsResultData search(EsQuery query) throws CustomSearchException {
    if (null == query || query.getIndexes().size() == 0) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    EsResultData result = new EsResultData();
    try {
      //添加index
      SearchRequestBuilder searchRequest = client
          .prepareSearch(query.getIndexes().toArray(new String[]{}));
      //添加type
      if (query.getTables().size() > 0) {
        searchRequest.setTypes(query.getTables().toArray(new String[]{}));
      }
      //查询对象
      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      //创建查询
      createQuery(query, qb);

      //添加搜索查询添加
      searchRequest.setQuery(qb);
      //sort
      if (query.getSorts().size() > 0) {
        for (Map.Entry<String, SortOrder> sort : query.getSorts().entrySet()) {
          searchRequest.addSort(sort.getKey(), sort.getValue());
        }
      }
      //设置分页
      searchRequest.setFrom((query.getPageNum() - 1) * query.getPageSize());
      searchRequest.setSize(query.getPageSize());
      SearchResponse response = searchRequest.execute().actionGet();
      List<JSONObject> items = new ArrayList<JSONObject>();
      int count = 0;
      if (query.getFetchesFields().size() > 0) {
        //指定返回字段
        for (SearchHit obj : response.getHits()) {
          count++;
          JSONObject json = new JSONObject();
          for (String key : query.getFetchesFields()) {
            json.put(key, obj.getSourceAsMap().get(key));
          }
          items.add(json);
        }
      } else {
        for (SearchHit obj : response.getHits()) {
          count++;
          items.add(JSONObject.parseObject(obj.getSourceAsString()));
        }
      }
      result.setItems(items);
      result.setNum(count);
      result.setTotal(response.getHits().getTotalHits());
      return result;
    } catch (Exception e) {
      if ("no such index".equals(e.getMessage())) {
        return new EsResultData();
      }
      e.printStackTrace();
      result.setSuccess(false);
      result.setMsg(e.getMessage());
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_SEARCH_ERROR);
      throw new CustomSearchException();
    }
  }

  /**
   * 组装查询
   *
   * @param query
   * @param qb
   */
  private void createQuery(EsQuery query, BoolQueryBuilder qb) {
    //不分词AND
    if (query.getAndFields().size() > 0) {
      BoolQueryBuilder andQB = QueryBuilders.boolQuery();
      for (String field : query.getAndFields().keySet()) {
        andQB.must(QueryBuilders.termQuery(field, query.getAndFields().get(field)));
      }
      qb.must(andQB);
    }
    //不分词 AND NOT
    if (query.getAndNotFields().size() > 0) {
      BoolQueryBuilder andNotQB = QueryBuilders.boolQuery();
      for (String field : query.getAndNotFields().keySet()) {
        andNotQB.must(QueryBuilders.termQuery(field, query.getAndNotFields().get(field)));
      }
      qb.mustNot(andNotQB);
    }
    //不分词OR
    if (query.getOrFields().size() > 0) {
      BoolQueryBuilder orQB = QueryBuilders.boolQuery();
      for (Map<String, Object> map : query.getOrFields()) {
        for (Map.Entry<String, Object> field : map.entrySet()) {
          orQB.should(QueryBuilders.termQuery(field.getKey(), field.getValue()));
        }
      }
      qb.must(orQB);
    }
    //IN查询
    if (query.getFilterInFields().size() > 0) {
      BoolQueryBuilder inQB = QueryBuilders.boolQuery();
      for (Map.Entry<String, List<Object>> field : query.getFilterInFields().entrySet()) {
        inQB.filter(QueryBuilders.termsQuery(field.getKey(), field.getValue()));
      }
      qb.must(inQB);
    }
    //and LIKE查询
    if (query.getAndLikeFields().size() > 0) {
      BoolQueryBuilder likeQB = QueryBuilders.boolQuery();
      for (Map<String, Object> map : query.getAndLikeFields()) {
        for (Map.Entry<String, Object> field : map.entrySet()) {
          likeQB
              .must(new QueryStringQueryBuilder(field.getValue().toString()).field(field.getKey()));
        }
      }
      qb.must(likeQB);
    }
    //or LIKE查询
    if (query.getOrLikeFields().size() > 0) {
      BoolQueryBuilder likeQB = QueryBuilders.boolQuery();
      for (Map<String, Object> map : query.getOrLikeFields()) {
        for (Map.Entry<String, Object> field : map.entrySet()) {
          likeQB.should(
              new QueryStringQueryBuilder(field.getValue().toString()).field(field.getKey()));
        }
      }
      qb.must(likeQB);
    }
  }

  @Override
  public EsResultData searchLbs(EsQuery query, float lon, float lat, double scope, String field)
      throws
      CustomSearchException {
    if (null == query || query.getIndexes().size() == 0) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    if (StringUtils.isBlank(field)) {
      field = "location";
    }
    EsResultData result = new EsResultData();
    try {
      //添加index
      SearchRequestBuilder searchRequest = client
          .prepareSearch(query.getIndexes().toArray(new String[]{}));
      //添加type
      if (query.getTables().size() > 0) {
        searchRequest.setTypes(query.getTables().toArray(new String[]{}));
      }
      //查询对象
      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      //创建查询
      createQuery(query, qb);

      //地理位置搜索
      if (lon > 0 && lat > 0 && scope > 0) {
        QueryBuilder geoQB = QueryBuilders.geoDistanceQuery(field).point(lat, lon)
            .distance(scope, DistanceUnit.KILOMETERS).geoDistance(GeoDistance.ARC);
        qb.must(geoQB);
        GeoDistanceSortBuilder sort = new GeoDistanceSortBuilder(field, lat, lon);
        sort.unit(DistanceUnit.KILOMETERS);// 距离单位公里
        sort.order(SortOrder.ASC);
        sort.point(lat, lon);// 注意纬度在前，经度在后
        searchRequest.addSort(sort);
      }

      //添加搜索查询添加
      searchRequest.setQuery(qb);

      //设置分页
      searchRequest.setFrom((query.getPageNum() - 1) * query.getPageSize());
      searchRequest.setSize(query.getPageSize());

      SearchResponse response = searchRequest.execute().actionGet();
      List<JSONObject> items = new ArrayList<JSONObject>();
      int count = 0;
      if (query.getFetchesFields().size() > 0) {
        //指定返回字段
        for (SearchHit obj : response.getHits()) {
          count++;
          JSONObject json = new JSONObject();
          for (String key : query.getFetchesFields()) {
            json.put(key, obj.getSourceAsMap().get(key));
          }
          items.add(json);
        }
      } else {
        for (SearchHit obj : response.getHits()) {
          count++;
          items.add(JSONObject.parseObject(obj.getSourceAsString()));
        }
      }
      result.setItems(items);
      result.setNum(count);
      result.setTotal(response.getHits().getTotalHits());
    } catch (Exception e) {
      if ("no such index".equals(e.getMessage())) {
        return new EsResultData();
      }
      result.setSuccess(false);
      result.setMsg(e.getMessage());
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_SEARCHLBS_ERROR);
      throw new CustomSearchException();
    }
    return result;
  }

  @Override
  public EsResultData searchAggregate(EsQuery query, String groupField, String aggField,
      String aggFun) throws
      CustomSearchException {
    if (null == query || query.getIndexes().size() == 0) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }
    EsResultData result = new EsResultData();
    try {
      //添加index
      SearchRequestBuilder searchRequest = client
          .prepareSearch(query.getIndexes().toArray(new String[]{}));
      //添加type
      if (query.getTables().size() > 0) {
        searchRequest.setTypes(query.getTables().toArray(new String[]{}));
      }
      //查询对象
      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      //创建查询
      createQuery(query, qb);

      //添加搜索查询添加
      searchRequest.setQuery(qb);

      //统计
      if (EsQuery.SUM.equals(aggFun)) {
        searchRequest
            .addAggregation(AggregationBuilders.terms(groupField).field(groupField).subAggregation
                (AggregationBuilders.sum(aggField).field(aggField)));
      }
      SearchResponse response = searchRequest.execute().actionGet();
      List<JSONObject> items = new ArrayList<JSONObject>();
      Terms terms = response.getAggregations().get(groupField);
      List<? extends Terms.Bucket> buckets = terms.getBuckets();
      int count = 0;
      for (Terms.Bucket bt : buckets) {
        count++;
        Sum sum = bt.getAggregations().get(aggField);
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        JSONObject json = new JSONObject();
        json.put(groupField, bt.getKey());
        json.put(aggField, nf.format(sum.getValue()));
        items.add(json);
      }
      result.setItems(items);
      result.setNum(count);
      result.setTotal(response.getHits().getTotalHits());
    } catch (Exception e) {
      if ("no such index".equals(e.getMessage())) {
        return new EsResultData();
      }
      result.setSuccess(false);
      result.setMsg(e.getMessage());
      log.error(e.getMessage(), e);
//            throw new CustomSearchException(CustomExceptionEnum.SEARCH_SEARCHLBS_ERROR);
      throw new CustomSearchException();
    }
    return result;
  }

  @Override
  public void add(String indexName, String tableName, String id, JSONObject json, String parent)
      throws CustomSearchException {
    if (null == json || StringUtils.isBlank(tableName) || StringUtils.isBlank(indexName)
        || id == null) {
      throw new CustomSearchException(SEARCH_PARAMS_ERR);
    }

    Map<String, Object> doc = new HashMap<String, Object>();
    //doc.put("id", id);
    for (String key : json.keySet()) {
      doc.put(key, json.get(key));
    }
    IndexRequest indexRequest = client.prepareIndex(indexName, tableName, id).setParent(parent)
        .setSource(doc)
        .request();

    client.prepareUpdate(indexName, tableName, id).setParent(parent).setDoc(doc)
        .setUpsert(indexRequest).get()
        .setForcedRefresh(true);
    ;
  }


}
