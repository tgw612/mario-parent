package com.mario.mysql;

import java.beans.PropertyDescriptor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class InsertIntoSqlNode {

  private static String INSERT_INTO = "INSERT INTO ";
  private static String SPLIT = ",";
  private static String rightBracket = ")";
  private static String leftBracket = "(";
  private static String valuesStr = " VALUES ";
  private static String onDeplicateupadteStr = " ON DUPLICATE KEY UPDATE ";
  private StringBuffer insertSql;
  private String name;

  private InsertIntoSqlNode() {
  }

  public String getSql(String tableName) {
    return (new StringBuilder(100)).append(INSERT_INTO).append(tableName).append(this.insertSql)
        .toString();
  }

  public static InsertIntoSqlNode getInstance(String name, Class mappedClass) {
    InsertIntoSqlNode sqlNode = new InsertIntoSqlNode();
    sqlNode.setName(name);
    StringBuffer insertSql = tranFieldsToSql(mappedClass, true);
    sqlNode.setInsertSql(insertSql);
    return sqlNode;
  }

  public static InsertIntoSqlNode getInstance(String name, Class mappedClass,
      boolean isEnableDuplicateUpdate) {
    InsertIntoSqlNode sqlNode = new InsertIntoSqlNode();
    sqlNode.setName(name);
    StringBuffer insertSql = tranFieldsToSql(mappedClass, isEnableDuplicateUpdate);
    sqlNode.setInsertSql(insertSql);
    return sqlNode;
  }

  private static <T> StringBuffer tranFieldsToSql(Class mappedClass,
      boolean isEnableDuplicateUpdate) {
    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
    if (pds.length <= 0) {
      return null;
    } else {
      StringBuffer insertSql = new StringBuffer(50);
      insertSql.append(leftBracket);
      StringBuilder mappedFieldSql = new StringBuilder(50);
      StringBuilder updateMappedFieldSql = new StringBuilder(50);
      PropertyDescriptor[] var6 = pds;
      int var7 = pds.length;

      for (int var8 = 0; var8 < var7; ++var8) {
        PropertyDescriptor pd = var6[var8];
        if (pd.getWriteMethod() != null) {
          String underscoredName = underScoreName(pd.getName());
          insertSql.append("`");
          insertSql.append(underscoredName);
          insertSql.append("`");
          insertSql.append(SPLIT);
          mappedFieldSql.append(":");
          mappedFieldSql.append(pd.getName());
          mappedFieldSql.append(SPLIT);
          if (isEnableDuplicateUpdate) {
            updateMappedFieldSql.append(underscoredName);
            updateMappedFieldSql.append("=:");
            updateMappedFieldSql.append(pd.getName());
            updateMappedFieldSql.append(SPLIT);
          }
        }
      }

      insertSql.delete(insertSql.length() - SPLIT.length(), insertSql.length());
      insertSql.append(rightBracket);
      insertSql.append(valuesStr);
      insertSql.append(leftBracket);
      insertSql.append(mappedFieldSql.substring(0, mappedFieldSql.length() - SPLIT.length()));
      insertSql.append(rightBracket);
      if (isEnableDuplicateUpdate) {
        insertSql.append(onDeplicateupadteStr);
        insertSql.append(
            updateMappedFieldSql.substring(0, updateMappedFieldSql.length() - SPLIT.length()));
      }

      return insertSql;
    }
  }

  private static String underScoreName(String name) {
    if (!StringUtils.hasLength(name)) {
      return "";
    } else {
      StringBuilder result = new StringBuilder();
      result.append(name.substring(0, 1).toLowerCase());

      for (int i = 1; i < name.length(); ++i) {
        String s = name.substring(i, i + 1);
        String slc = s.toLowerCase();
        if (!s.equals(slc)) {
          result.append("_").append(slc);
        } else {
          result.append(s);
        }
      }

      return result.toString();
    }
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StringBuffer getInsertSql() {
    return this.insertSql;
  }

  public void setInsertSql(StringBuffer insertSql) {
    this.insertSql = insertSql;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      InsertIntoSqlNode sqlNode = (InsertIntoSqlNode) o;
      return this.name.equals(sqlNode.name);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}