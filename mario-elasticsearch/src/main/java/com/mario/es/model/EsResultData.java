package com.mario.es.model;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * 搜索返回结果 https://help.aliyun.com/document_detail/29150.html?spm=5176.doc29168.2.3.HGnYuh
 */
@Data
@ToString
public class EsResultData implements Serializable {

  private static final long serialVersionUID = 5853492468583348551L;

  /**
   * 引擎耗时，单位为秒。
   */
  private float searchtime;

	/*
	total、viewtotal、num区别：total为一次查询（不考虑config子句）引擎中符合条件的结果数（在结果数较多情况下，该值会做优化），
	但考虑到性能及相关性，引擎最多会返回viewtotal个结果，如果需要翻页的话，要求start+hit一定要小于viewtotal，total一般用来做展示。
	num为本次查询请求（受config子句的start及hit）实际返回的条目，不会超过hit值。
	*/

  /**
   * 搜索到结果数
   */
  private long total;

  /**
   * 返回结果数
   */
  private long num;

  private long viewtotal;

  /**
   * 返回结果集
   * <p>
   * 包含两个节点fields及variableValue，其中fields为搜索返回字段内容，variableValue为自定义参数返回结果，如获取distance距离值。
   * variableValue节点只有在config子句的format为“xml”或者“fulljson”时才能展现出来，“json”格式默认不展示。
   */
  private List<JSONObject> items;

  /**
   * 统计结果
   */
  private List<JSONObject> facet;

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 查询异常信息
   */
  private String msg;
}
