package com.mario.es.service;

import com.alibaba.fastjson.JSONObject;
import com.mario.es.exception.CustomSearchException;
import com.mario.es.model.EsQuery;
import com.mario.es.model.EsResultData;

public interface EsSearchService {

  /**
   * 添加文档
   *
   * @param indexName 应用名（库名）
   * @param tableName 表名
   * @param id        文档id
   * @param json      文档对象
   */
  public void add(String indexName, String tableName, String id, JSONObject json)
      throws CustomSearchException;

  public void add(String indexName, String tableName, String id, JSONObject json, String parent)
      throws CustomSearchException;

  //void asyncAdd(String indexName, String tableName, Collection<String> id, JSONObject... json);


  /**
   * 添加或更新
   *
   * @param indexName
   * @param tableName
   * @param id
   * @param json
   * @throws CustomSearchException
   */
  public void upsert(String indexName, String tableName, String id, JSONObject json)
      throws CustomSearchException;

  /**
   * 更新文档
   *
   * @param indexName 应用名（库名）
   * @param tableName 表名
   * @param id        文档id
   * @param json      待更新文档
   */
  public void updateById(String indexName, String tableName, String id, JSONObject json)
      throws CustomSearchException;

  /**
   * 删除文档
   *
   * @param indexName
   * @param tableName
   * @param id
   * @throws CustomSearchException
   */
  public void deleteById(String indexName, String tableName, String id)
      throws CustomSearchException;

  /**
   * 根据id获取文档
   *
   * @param indexName
   * @param tableName
   * @param id
   * @return
   * @throws CustomSearchException
   */
  public JSONObject getById(String indexName, String tableName, String id)
      throws CustomSearchException;

  /**
   * 执行搜索请求
   *
   * @return String 返回搜索结果(json)。
   * @throws CustomSearchException
   */
  public EsResultData search(EsQuery query) throws CustomSearchException;

  /**
   * 地理位置搜索
   *
   * @param query 查询条件
   * @param lon
   * @param lat
   * @param scope 查询范围，单位km
   * @param field 位置字段
   * @return
   * @throws CustomSearchException
   */
  public EsResultData searchLbs(EsQuery query, float lon, float lat, double scope, String field)
      throws CustomSearchException;


  /**
   * 聚合查询测试
   *
   * @param query
   * @param groupField group字段
   * @param aggField   统计字段
   * @param aggFun     指定统计的方法。参考EsQuery对象
   * @return
   * @throws CustomSearchException
   */

  public EsResultData searchAggregate(EsQuery query, String groupField, String aggField,
      String aggFun) throws CustomSearchException;
}
