//package com.mario.mysql.jdbc;
//
//import com.mario.common.model.request.PageQueryRequest;
//import com.mario.common.model.response.CommonPageResult;
//import com.mario.common.util.ExceptionUtil;
//import java.sql.PreparedStatement;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import javax.sql.DataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.CallableStatementCallback;
//import org.springframework.jdbc.core.CallableStatementCreator;
//import org.springframework.jdbc.core.ColumnMapRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCallback;
//import org.springframework.jdbc.core.SingleColumnRowMapper;
//import org.springframework.jdbc.core.SqlParameter;
//import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.namedparam.SqlParameterSource;
//
//public class JdbcTemplateUtil {
//
//  private static final Logger log = LoggerFactory.getLogger(JdbcTemplateUtil.class);
//  private static final String QUERY_COUNT_FROM = "SELECT COUNT(1) FROM ";
//  private static final String AND_SPLIT = " AND ";
//  public static NamedParameterJdbcTemplate npJdbcTemplate;
//  public static org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
//
//  public JdbcTemplateUtil() {
//  }
//
//  public static boolean update(String sql, Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate.update(sql, paramMap) > 0;
//    } catch (DataAccessException var3) {
//      log.error("Update sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var3)});
//      throw var3;
//    }
//  }
//
//  public static <T> boolean update(String sql, T beanParam) {
//    BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(beanParam);
//
//    try {
//      return npJdbcTemplate.update(sql, ps) > 0;
//    } catch (DataAccessException var4) {
//      log.error("Update sql:[{}] param:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, beanParam, ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> int[] batchUpdate(String sql, Map<String, Object>[] paramMapArr) {
//    return batchUpdate(sql, (Map[]) paramMapArr, 200);
//  }
//
//  public static <T> int[] batchUpdate(String sql, Map<String, Object>[] paramMapArr,
//      int batchSize) {
//    if (batchSize <= 0) {
//      throw new IllegalArgumentException(
//          "Excute method[JdbcTemplate.batchUpdate], the batchSize is less or eq 0");
//    } else {
//      int totalSize = paramMapArr.length;
//      int[] result = new int[totalSize];
//      if (totalSize > 0) {
//        int arrBatchSize = Math.min(totalSize, batchSize);
//        int beanParamArrIndex = 0;
//        int resultDestPos = 0;
//
//        try {
//          do {
//            SqlParameterSource[] batchArgs = new SqlParameterSource[arrBatchSize];
//
//            for (int i = 0; i < arrBatchSize; ++i) {
//              batchArgs[i] = new MapSqlParameterSource(paramMapArr[beanParamArrIndex++]);
//            }
//
//            int[] updateResult = npJdbcTemplate.batchUpdate(sql, batchArgs);
//            System.arraycopy(updateResult, 0, result, resultDestPos, updateResult.length);
//            resultDestPos += updateResult.length;
//          } while ((arrBatchSize = Math.min(totalSize -= arrBatchSize, batchSize)) > 0);
//        } catch (DataAccessException var10) {
//          log.error("Batch update sql:[{}] params size:{}.\nSome Exception Occur:[{}]",
//              new Object[]{sql, paramMapArr != null ? paramMapArr.length : 0,
//                  ExceptionUtil.getAsString(var10)});
//          throw ExceptionUtil.translateDataBaseException();
//        }
//      }
//
//      return result;
//    }
//  }
//
//  public static <T> int[] batchUpdate(String sql, T[] beanParamArr) {
//    return batchUpdate(sql, (Object[]) beanParamArr, 200);
//  }
//
//  public static <T> int[] batchUpdate(String sql, T[] beanParamArr, int batchSize) {
//    if (batchSize <= 0) {
//      throw new IllegalArgumentException(
//          "Excute method[JdbcTemplate.batchUpdate], the batchSize is less or eq 0");
//    } else {
//      int totalSize = beanParamArr.length;
//      int[] result = new int[totalSize];
//      if (totalSize > 0) {
//        int arrBatchSize = Math.min(totalSize, batchSize);
//        int beanParamArrIndex = 0;
//        int resultDestPos = 0;
//
//        try {
//          do {
//            SqlParameterSource[] batchArgs = new SqlParameterSource[arrBatchSize];
//
//            for (int i = 0; i < arrBatchSize; ++i) {
//              batchArgs[i] = new BeanPropertySqlParameterSource(beanParamArr[beanParamArrIndex++]);
//            }
//
//            int[] updateResult = npJdbcTemplate.batchUpdate(sql, batchArgs);
//            System.arraycopy(updateResult, 0, result, resultDestPos, updateResult.length);
//            resultDestPos += updateResult.length;
//          } while ((arrBatchSize = Math.min(totalSize -= arrBatchSize, batchSize)) > 0);
//        } catch (DataAccessException var10) {
//          log.error("Batch update sql:[{}] params size:{}.\nSome Exception Occur:[{}]",
//              new Object[]{sql, beanParamArr != null ? beanParamArr.length : 0,
//                  ExceptionUtil.getAsString(var10)});
//          throw ExceptionUtil.translateDataBaseException();
//        }
//      }
//
//      return result;
//    }
//  }
//
//  public static <T> int[] batchUpdate(String sql, List<T> beanParamArr) {
//    return batchUpdate(sql, (List) beanParamArr, 200);
//  }
//
//  public static <T> int[] batchUpdate(String sql, List<T> beanParamArr, int batchSize) {
//    if (batchSize <= 0) {
//      throw new IllegalArgumentException(
//          "Excute method[JdbcTemplate.batchUpdate], the batchSize is less or eq 0");
//    } else {
//      int totalSize = beanParamArr.size();
//      int[] result = new int[totalSize];
//      if (totalSize > 0) {
//        int arrBatchSize = Math.min(totalSize, batchSize);
//        int beanParamArrIndex = 0;
//        int resultDestPos = 0;
//
//        try {
//          do {
//            SqlParameterSource[] batchArgs = new SqlParameterSource[arrBatchSize];
//
//            for (int i = 0; i < arrBatchSize; ++i) {
//              batchArgs[i] = new BeanPropertySqlParameterSource(
//                  beanParamArr.get(beanParamArrIndex));
//              ++beanParamArrIndex;
//            }
//
//            int[] updateResult = npJdbcTemplate.batchUpdate(sql, batchArgs);
//            System.arraycopy(updateResult, 0, result, resultDestPos, updateResult.length);
//            resultDestPos += updateResult.length;
//          } while ((arrBatchSize = Math.min(totalSize -= arrBatchSize, batchSize)) > 0);
//        } catch (DataAccessException var10) {
//          log.error("Batch update sql:[{}] params size:{}.\nSome Exception Occur:[{}]",
//              new Object[]{sql, beanParamArr != null ? beanParamArr.size() : 0,
//                  ExceptionUtil.getAsString(var10)});
//          throw ExceptionUtil.translateDataBaseException();
//        }
//      }
//
//      return result;
//    }
//  }
//
//  public static <T> T queryForObject(String sql, Class<T> clazz, Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate.queryForObject(sql, paramMap, BeanPropertyRowMapper.newInstance(clazz));
//    } catch (DataAccessException var4) {
//      log.error("Query for object[{}] sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{clazz, sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> T queryForObject(String sql, Class<T> clazz) {
//    return queryForObject(sql, clazz, (Map) null);
//  }
//
//  public static <T> T queryProperty(String sql, Class<T> propertyType,
//      Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate.queryForObject(sql, paramMap, new SingleColumnRowMapper(propertyType));
//    } catch (DataAccessException var4) {
//      log.error(
//          "Query for object.property[{}] sql:[{}] param size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{propertyType, sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> T queryProperty(String sql, Class<T> propertyType) {
//    return queryForObject(sql, propertyType, (Map) null);
//  }
//
//  public static long queryForLong(String sql, Map<String, Object> paramMap) {
//    Number number = (Number) queryProperty(sql, Long.class, paramMap);
//    return number != null ? number.longValue() : 0L;
//  }
//
//  public static long queryForLong(String sql) {
//    return queryForLong(sql, (Map) null);
//  }
//
//  public static int queryForInt(String sql, Map<String, Object> paramMap) {
//    Number number = (Number) queryProperty(sql, Integer.class, paramMap);
//    return number != null ? number.intValue() : 0;
//  }
//
//  public static int queryForInt(String sql) {
//    return queryForInt(sql, (Map) null);
//  }
//
//  public static String queryForString(String sql, Map<String, Object> paramMap) {
//    return (String) queryProperty(sql, String.class, paramMap);
//  }
//
//  public static String queryForString(String sql) {
//    return queryForString(sql, (Map) null);
//  }
//
//  public static Map<String, Object> queryForMap(String sql, Map<String, Object> paramMap) {
//    try {
//      return (Map) npJdbcTemplate.queryForObject(sql, paramMap, new ColumnMapRowMapper());
//    } catch (DataAccessException var3) {
//      log.error("Query for Map sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var3)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static Map<String, Object> queryForMap(String sql) {
//    return queryForMap(sql, (Map) null);
//  }
//
//  public static <T> List<T> queryForBeanList(String sql, Class<T> clazz,
//      Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper(clazz));
//    } catch (DataAccessException var4) {
//      log.error("Query for object List sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> List<T> queryForBeanList(String sql, Class<T> clazz) {
//    return queryForBeanList(sql, clazz, (Map) null);
//  }
//
//  public static <T> List<T> queryForPropertyList(String sql, Class<T> property,
//      Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate.queryForList(sql, paramMap, property);
//    } catch (DataAccessException var4) {
//      log.error("Query for property List sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> List<T> queryForPropertyList(String sql, Class<T> property) {
//    return queryForPropertyList(sql, property, (Map) null);
//  }
//
//  public List<Map<String, Object>> queryForList(String sql, Map<String, Object> paramMap) {
//    try {
//      return npJdbcTemplate
//          .query(sql, new MapSqlParameterSource(paramMap), new ColumnMapRowMapper());
//    } catch (DataAccessException var4) {
//      log.error(
//          "Query for List<Map<String, Object>> sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public List<Map<String, Object>> queryForList(String sql) {
//    return this.queryForList(sql, (Map) null);
//  }
//
//  public static long count(String table, Map<String, Object> paramMap) {
//    String whereSql = createWhereSqlByMapParam(paramMap.keySet());
//    StringBuilder sql = new StringBuilder(50);
//    sql.append("SELECT COUNT(1) FROM ").append(" " + table + " ").append(whereSql);
//
//    try {
//      return (Long) npJdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
//    } catch (DataAccessException var5) {
//      log.error("Count sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var5)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static long count(String table) {
//    StringBuilder sql = new StringBuilder();
//    sql.append("SELECT COUNT(1) FROM ").append(" " + table);
//    Object paramMap = null;
//
//    try {
//      return (Long) npJdbcTemplate.queryForObject(sql.toString(), (Map) paramMap, Long.class);
//    } catch (DataAccessException var4) {
//      log.error("Count sql:[{}].\nSome Exception Occur:[{}]", sql, ExceptionUtil.getAsString(var4));
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> T execute(String sql, Map<String, Object> paramMap,
//      PreparedStatementCallback<T> action) {
//    try {
//      return npJdbcTemplate.execute(sql, paramMap, action);
//    } catch (DataAccessException var4) {
//      log.error("Execute for sql:[{}] param map size:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramMap != null ? paramMap.keySet().size() : 0,
//              ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> T execute(String sql, Object paramBean, PreparedStatementCallback<T> action) {
//    try {
//      return npJdbcTemplate.execute(sql, new BeanPropertySqlParameterSource(paramBean), action);
//    } catch (DataAccessException var4) {
//      log.error("Execute for sql:[{}] param:[{}].\nSome Exception Occur:[{}]",
//          new Object[]{sql, paramBean, ExceptionUtil.getAsString(var4)});
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static Map<String, Object> call(CallableStatementCreator csc,
//      List<SqlParameter> declaredParameters) {
//    try {
//      return jdbcTemplate.call(csc, declaredParameters);
//    } catch (DataAccessException var3) {
//      log.error("Call for procedures or functions.\nSome Exception Occur:[{}]",
//          ExceptionUtil.getAsString(var3));
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static List<Map<String, String>> execute(String callString,
//      CallableStatementCallback<List<Map<String, String>>> action) {
//    try {
//      return (List) jdbcTemplate.execute(callString, action);
//    } catch (DataAccessException var3) {
//      log.error("Call for procedures or functions.\nSome Exception Occur:[{}]",
//          ExceptionUtil.getAsString(var3));
//      throw ExceptionUtil.translateDataBaseException();
//    }
//  }
//
//  public static <T> CommonPageResult<T> queryForBeanPage(String tableName, Class<T> clazz,
//      Map<String, Object> paramMap, PageQueryRequest page) {
//    int totalCount = (int) count(tableName, paramMap);
//    CommonPageResult<T> pageResult = new CommonPageResult();
//    if (totalCount > 0) {
//      int totalPage =
//          totalCount / page.getPageSize() + (totalCount % page.getPageSize() == 0 ? 0 : 1);
//      pageResult.setTotalPage((long) totalPage);
//      pageResult.setTotalCount((long) totalCount);
//      if (page.getCurrentPage() <= totalPage) {
//        StringBuilder pageSql = new StringBuilder(100);
//        pageSql.append("select * from ").append(tableName).append(" ");
//        String whereSql = createWhereSqlByMapParam(paramMap.keySet());
//        pageSql.append(whereSql);
//        String beginrow = String.valueOf((page.getCurrentPage() - 1) * page.getPageSize());
//        pageSql.append(" limit " + beginrow + "," + page.getPageSize());
//        List<T> tList = queryForBeanList(pageSql.toString(), clazz, paramMap);
//        pageResult.setData(tList);
//      }
//    }
//
//    return pageResult;
//  }
//
//  public static String createWhereSqlByMapParam(Set<String> fields) {
//    if (fields.isEmpty()) {
//      return "";
//    } else {
//      StringBuilder result = new StringBuilder();
//      result.append(" WHERE ");
//      Iterator var2 = fields.iterator();
//
//      while (var2.hasNext()) {
//        String field = (String) var2.next();
//        result.append(field.substring(0, 1).toLowerCase());
//
//        for (int i = 1; i < field.length(); ++i) {
//          String s = field.substring(i, i + 1);
//          String slc = s.toLowerCase();
//          if (!s.equals(slc)) {
//            result.append("_").append(slc);
//          } else {
//            result.append(s);
//          }
//        }
//
//        result.append("=:");
//        result.append(field);
//        result.append(" AND ");
//      }
//
//      return result.substring(0, result.length() - " AND ".length());
//    }
//  }
//
//  private static void createSqlParams(Object paramsData, PreparedStatement preparedStatement) {
//    Object obj = null;
//    List paramList = null;
//
//    try {
//      paramList = (List) paramsData;
//
//      for (int i = 0; i < paramList.size(); ++i) {
//        obj = paramList.get(i);
//        if (obj instanceof String) {
//          preparedStatement.setString(i + 1, String.valueOf(obj));
//        } else if (obj instanceof Integer) {
//          preparedStatement.setInt(i + 1, (Integer) obj);
//        } else if (obj instanceof Date) {
//          preparedStatement.setTimestamp(i + 1, new Timestamp(((Date) obj).getTime()));
//        } else if (null == obj) {
//          preparedStatement.setObject(i + 1, (Object) null);
//        } else {
//          preparedStatement.setString(i + 1, String.valueOf(obj));
//        }
//      }
//    } catch (Exception var5) {
//      log.error("createSqlParams error!", ExceptionUtil.getAsString(var5));
//    }
//
//  }
//
//  public void setJdbcTemplate(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
//    jdbcTemplate(jdbcTemplate);
//  }
//
//  public static org.springframework.jdbc.core.JdbcTemplate getJdbcTemplate() {
//    return jdbcTemplate;
//  }
//
//  public static DataSource getDataSource() {
//    return getJdbcTemplate().getDataSource();
//  }
//
//  public static void jdbcTemplate(JdbcTemplate jdbcTemplate) {
//    JdbcTemplateUtil.jdbcTemplate = jdbcTemplate;
//    npJdbcTemplate = new INamedParameterJdbcTemplate(jdbcTemplate);
//  }
//}
