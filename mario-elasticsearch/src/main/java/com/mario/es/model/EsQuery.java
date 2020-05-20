package com.mario.es.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;

/**
 * 搜索查询条件对象
 */
public class EsQuery implements Serializable {

  /**
   * 聚合统计累加
   */
  public static final String SUM = "sum";

  /**
   * AND查询条件 eg: 字段1的值为value field1:'value'
   */
  @Getter
  private Map<String, Object> andFields = new HashMap<String, Object>();

  /**
   * AND NOT查询
   */
  @Getter
  private Map<String, Object> andNotFields = new HashMap<String, Object>();

  /**
   * OR查询条件 eg: 字段1的值为value field1:'value'
   */
  @Getter
  private List<Map<String, Object>> orFields = new ArrayList<Map<String, Object>>();

  /**
   * and LIKE查询条件 eg: 字段1的值为value field1:'value'
   */
  @Getter
  private List<Map<String, Object>> andLikeFields = new ArrayList<Map<String, Object>>();

  /**
   * or LIKE查询条件 eg: 字段1的值为value field1:'value'
   */
  @Getter
  private List<Map<String, Object>> orLikeFields = new ArrayList<Map<String, Object>>();

  /**
   * IN查询条件
   */
  @Getter
  private Map<String, List<Object>> filterInFields = new HashMap<String, List<Object>>();


  /**
   * 排序字段 eg: 字段1升序 field1:SORT_INCREASE
   */
  @Getter
  private Map<String, SortOrder> sorts = new LinkedHashMap<String, SortOrder>();

  /**
   * 检索应用名称。 可以指定单个应用名称，也可以指定多个应用名称来进行搜索。
   */
  @Getter
  private Set<String> indexes = new HashSet<String>();

  @Getter
  private Set<String> tables = new HashSet<String>();

  /**
   * 指定返回字段
   */
  @Getter
  private List<String> fetchesFields = new ArrayList<String>();

  /**
   * 当前页
   */
  private Integer pageNum = 1;
  /**
   * 每页数据条数
   */
  private Integer pageSize = 10;


  /**
   * 添加AND查询条件
   *
   * @param field 字段名称
   * @param value 字段值
   */
  public void addAndField(String field, Object value) {
    if (!StringUtils.isEmpty(field) && !StringUtils.isEmpty(value)) {
      andFields.put(field, value);
    }
  }

  /**
   * 添加AND NOT查询条件
   *
   * @param field
   * @param value
   */
  public void addAndNotField(String field, Object value) {
    if (!StringUtils.isEmpty(field) && null != value) {
      andNotFields.put(field, value);
    }
  }

  /**
   * 添加OR查询条件
   *
   * @param field 字段名称
   * @param value 字段值
   */
  public void addOrField(String field, Object value) {
    if (!StringUtils.isEmpty(field) && !StringUtils.isEmpty(value)) {
      Map<String, Object> map = new HashMap<>();
      map.put(field, value);
      orFields.add(map);
    }
  }

  /**
   * 添加and like查询条件
   *
   * @param field 字段名称
   * @param value 字段值
   */
  public void addAndLikeField(String field, Object value) {
    if (!StringUtils.isEmpty(field) && null != value) {
      Map<String, Object> map = new HashMap<>();
      map.put(field, value);
      andLikeFields.add(map);
    }
  }

  /**
   * 添加or like查询条件
   *
   * @param field 字段名称
   * @param value 字段值
   */
  public void addOrLikeField(String field, Object value) {
    if (!StringUtils.isEmpty(field) && null != value) {
      Map<String, Object> map = new HashMap<>();
      map.put(field, value);
      orLikeFields.add(map);
    }
  }

  /**
   * 添加排序条件
   *
   * @param field 排序字段名称
   * @param sort  排序方式
   */
  public void addSort(String field, SortOrder sort) {
    if (!StringUtils.isEmpty(field) && !StringUtils.isEmpty(sort)) {
      sorts.put(field, sort);
    }
  }

  /**
   * 添加排序条件（默认倒序）
   *
   * @param field
   */
  public void addSort(String field) {
    if (!StringUtils.isEmpty(field)) {
      sorts.put(field, SortOrder.DESC);
    }
  }

  /**
   * 添加in查询条件
   *
   * @param field
   * @param values
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void addFilterInField(String field, List values) {
    if (!StringUtils.isEmpty(field) && values != null && values.size() > 0) {
      filterInFields.put(field, values);
    }
  }

  /**
   * 添加返回字段
   *
   * @param fields
   */
  public void addfetches(String... fields) {
    if (!StringUtils.isEmpty(fields)) {
      for (String field : fields) {
        if (!StringUtils.isEmpty(field)) {
          fetchesFields.add(field);
        }
      }
    }
  }

  /**
   * 查询应用名
   *
   * @param indexName
   */
  public void addIndex(String indexName) {
    if (!StringUtils.isEmpty(indexName)) {
      indexes.add(indexName);
    }
  }

  /**
   * 查询应用表名
   *
   * @param table
   */
  public void addTable(String table) {
    if (!StringUtils.isEmpty(table)) {
      tables.add(table);
    }
  }


  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

}
