package com.mario.mysql.loaddata;

import com.mario.common.annotation.TransientNode;
import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.SystemException;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.DateUtil;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.ReflectionUtil;
import com.mario.common.util.StringUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class LoadDataInFileMySqlNode<T> {

  private static final Logger log = LoggerFactory.getLogger(LoadDataInFileMySqlNode.class);
  private static final String DEFAULT_CHARACTER_SET = "utf8mb4";
  private static final String NULL = "\\N";
  private static final String DEFAULT_ENCLOSED_BY = "'";
  private static final String DEFAULT_FIELDS_TERMINATED_BY = ",";
  private static final String DEFAULT_LINES_TERMINATED_BY = "\r\n";
  private static final String DEFAULT_RIGHT_BRACKET = ")";
  private static final String DEFAULT_LEFT_BRACKET = "(";
  private static final String DEFAULT_SPLIT = ",";
  private static final boolean DEFAULT_LOW_PRIORITY = false;
  private static final boolean DEFAULT_IGNORE = false;
  private final boolean ignore;
  private final String enclosedBy;
  private final String fieldsTerminatedBy;
  private final String linesTerminatedBy;
  private final String loadDataInFile2Mysql;
  private List<PropertyDescriptor> pdsList;
  private String dataFormatPattern;
  private String tableName;

  public LoadDataInFileMySqlNode(Class<T> tClass) {
    this(tClass, "DEFAULT_TABLE", false, "utf8mb4", "'", ",", "\r\n", false, "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, boolean ignore, boolean isLowPriority) {
    this(tClass, "DEFAULT_TABLE", ignore, "utf8mb4", "'", ",", "\r\n", isLowPriority,
        "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore) {
    this(tClass, tableName, ignore, "utf8mb4", "'", ",", "\r\n", false, "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      boolean isLowPriority) {
    this(tClass, tableName, ignore, "utf8mb4", "'", ",", "\r\n", isLowPriority,
        "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      String characterSet) {
    this(tClass, tableName, ignore, characterSet, "'", ",", "\r\n", false, "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      String characterSet, boolean isLowPriority) {
    this(tClass, tableName, ignore, characterSet, "'", ",", "\r\n", isLowPriority,
        "yyyy-MM-dd HH:mm:ss");
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      String characterSet, String dataFormatPattern) {
    this(tClass, tableName, ignore, characterSet, "'", ",", "\r\n", false, dataFormatPattern);
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      String characterSet, boolean isLowPriority, String dataFormatPattern) {
    this(tClass, tableName, ignore, characterSet, "'", ",", "\r\n", isLowPriority,
        dataFormatPattern);
  }

  public LoadDataInFileMySqlNode(Class<T> tClass, String tableName, boolean ignore,
      String characterSet, String enclosedBy, String fieldsTerminatedBy, String linesTerminatedBy,
      boolean isLowPriority, String dataFormatPattern) {
    this.pdsList = null;
    this.tableName = tableName;
    this.enclosedBy = enclosedBy;
    this.fieldsTerminatedBy = fieldsTerminatedBy;
    this.linesTerminatedBy = linesTerminatedBy;
    this.ignore = ignore;
    if (StringUtils.isNotBlank(dataFormatPattern)) {
      this.dataFormatPattern = dataFormatPattern;
    } else {
      this.dataFormatPattern = "yyyy-MM-dd HH:mm:ss";
    }

    this.loadDataInFile2Mysql = this
        .initLoadDataInFile2Mysql(tClass, tableName, ignore, isLowPriority, characterSet,
            this.translate(enclosedBy), this.translate(fieldsTerminatedBy),
            this.translate(linesTerminatedBy));
  }

  public String getLoadData2MySqlData(List<T> dataList) {
    if (!CollectionUtil.isNotEmpty(dataList)) {
      return "";
    } else {
      StringBuilder dataStr = new StringBuilder(100);
      int dataLen = dataList.size();
      int pdLen = this.pdsList.size();
      int lastPdLen = pdLen - 1;

      for (int j = 0; j < dataLen; ++j) {
        T t = dataList.get(j);

        for (int i = 0; i < pdLen; ++i) {
          try {
            Object value = ((PropertyDescriptor) this.pdsList.get(i)).getReadMethod().invoke(t);
            Object o = this.getStringValue(value);
            dataStr.append(this.enclosedBy).append(o).append(this.enclosedBy);
          } catch (Throwable var11) {
            log.error("Reflection to get value  Exception:{}", ExceptionUtil.getAsString(var11));
            throw new SystemException(CommonErrCodeEnum.BEAN_VLUE_ERROR, var11);
          }

          if (i < lastPdLen) {
            dataStr.append(this.fieldsTerminatedBy);
          }
        }

        dataStr.append(this.linesTerminatedBy);
      }

      return dataStr.toString();
    }
  }

  public String getLoadData2MySqlData(T... dataArr) {
    if (dataArr != null && dataArr.length > 0) {
      StringBuilder dataStr = new StringBuilder(100);
      int pdLen = this.pdsList.size();
      int lastPdLen = pdLen - 1;

      for (int j = 0; j < dataArr.length; ++j) {
        T t = dataArr[j];

        for (int i = 0; i < pdLen; ++i) {
          try {
            Object value = ((PropertyDescriptor) this.pdsList.get(i)).getReadMethod().invoke(t);
            Object o = this.getStringValue(value);
            dataStr.append(this.enclosedBy).append(o).append(this.enclosedBy);
          } catch (Throwable var10) {
            log.error("Reflection to get value  Exception:{}", ExceptionUtil.getAsString(var10));
            throw new SystemException(CommonErrCodeEnum.BEAN_VLUE_ERROR, var10);
          }

          if (i < lastPdLen) {
            dataStr.append(this.fieldsTerminatedBy);
          }
        }

        dataStr.append(this.linesTerminatedBy);
      }

      return dataStr.toString();
    } else {
      return "";
    }
  }

  public String getLoadDataInFile2Mysql(String tableName) {
    StringBuilder stringBuilder = new StringBuilder(this.loadDataInFile2Mysql.length());
    int i = this.loadDataInFile2Mysql.indexOf("character set");
    String charSequence = this.loadDataInFile2Mysql.substring(0, i);
    String replace = charSequence.replace(this.tableName, tableName);
    return stringBuilder.append(replace).append(this.loadDataInFile2Mysql.substring(i)).toString();
  }

  public String getLoadDataInFile2Mysql() {
    return this.loadDataInFile2Mysql;
  }

  private Object getStringValue(Object value) {
    if (value != null) {
      if (value instanceof Boolean) {
        return (Boolean) value ? '\u0001' : '\u0000';
      } else if (value instanceof String) {
        return value.toString().replace(this.enclosedBy, this.enclosedBy + this.enclosedBy)
            .replace("\\", "\\\\");
      } else if (value instanceof Date) {
        return DateUtil.formatDate((Date) value, this.dataFormatPattern);
      } else {
        return value instanceof java.sql.Date ? DateUtil
            .formatDate((java.sql.Date) value, this.dataFormatPattern) : value.toString();
      }
    } else {
      return "\\N";
    }
  }

  private String initLoadDataInFile2Mysql(Class mappedClass, String tableName, boolean ignore,
      boolean isLowPriority, String characterSet, String enclosedBy, String fieldsTerminatedBy,
      String linesTerminatedBy) {
    StringBuffer loadDataInFile2Mysql = new StringBuffer(100);
    if (isLowPriority) {
      loadDataInFile2Mysql.append("load data low_priority local infile '");
    } else {
      loadDataInFile2Mysql.append("load data local infile '");
    }

    if (ignore) {
      loadDataInFile2Mysql.append(tableName).append(".db' ignore into table ").append(tableName);
    } else {
      loadDataInFile2Mysql.append(tableName).append(".db' replace into table ").append(tableName);
    }

    loadDataInFile2Mysql.append(" character set '").append(characterSet).append("'");
    loadDataInFile2Mysql.append(" fields terminated by '").append(fieldsTerminatedBy).append("'");
    loadDataInFile2Mysql.append(" enclosed by '").append(enclosedBy).append("'");
    loadDataInFile2Mysql.append(" lines terminated by '").append(linesTerminatedBy).append("' ");
    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
    this.pdsList = new ArrayList(pds.length);
    if (pds.length <= 0) {
      return null;
    } else {
      loadDataInFile2Mysql.append("(");
      PropertyDescriptor[] var11 = pds;
      int var12 = pds.length;

      for (int var13 = 0; var13 < var12; ++var13) {
        PropertyDescriptor pd = var11[var13];
        if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
          Field accessibleField = ReflectionUtil.getAccessibleField(mappedClass, pd.getName());
          if ((accessibleField == null || !accessibleField.isAnnotationPresent(TransientNode.class))
              && !pd.getReadMethod().isAnnotationPresent(TransientNode.class)) {
            String underscoredName = underScoreName(pd.getName());
            loadDataInFile2Mysql.append("`");
            loadDataInFile2Mysql.append(underscoredName);
            loadDataInFile2Mysql.append("`");
            loadDataInFile2Mysql.append(",");
            this.pdsList.add(pd);
          }
        }
      }

      loadDataInFile2Mysql
          .delete(loadDataInFile2Mysql.length() - ",".length(), loadDataInFile2Mysql.length());
      loadDataInFile2Mysql.append(")");
      return loadDataInFile2Mysql.toString();
    }
  }

  private static String underScoreName(String name) {
    if (!StringUtil.hasLength(name)) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      LoadDataInFileMySqlNode sqlNode = (LoadDataInFileMySqlNode) o;
      return this.tableName.equals(sqlNode.tableName) && this.loadDataInFile2Mysql
          .equals(sqlNode.loadDataInFile2Mysql);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.tableName.hashCode() + this.loadDataInFile2Mysql.hashCode();
  }

  private String translate(String str) {
    return str.replace("\\", "\\\\").replace("'", "\\'");
  }

  @Override
  public String toString() {
    return "LoadDataInFileMySqlNode{linesTerminatedBy='" + this.linesTerminatedBy + '\''
        + ", enclosedBy='" + this.enclosedBy + '\'' + ", fieldsTerminatedBy='"
        + this.fieldsTerminatedBy + '\'' + ", tableName='" + this.tableName + '\''
        + ", loadDataInFile2Mysql='" + this.loadDataInFile2Mysql + '\'' + '}';
  }
}