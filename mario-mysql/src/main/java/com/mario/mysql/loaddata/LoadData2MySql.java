package com.mario.mysql.loaddata;

import com.mario.common.exception.DataBaseOperateException;
import com.mario.common.util.ExceptionUtil;
import com.mysql.jdbc.Statement;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.CodecException;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class LoadData2MySql {

  private static final Logger log = LoggerFactory.getLogger(LoadData2MySql.class);
  private DataSource dataSource;

  public LoadData2MySql() {
  }

  public LoadData2MySql(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public int importDataToMySql(String loadDataSql, String dataContent)
      throws DataBaseOperateException {
    try {
      return this.importDataToMySql(loadDataSql, dataContent.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException var5) {
      String msg = "Unable to convert source size:" + dataContent.length()
          + " to byte array using encoding '" + "UTF-8" + "'";
      log.error("{}", msg);
      throw new CodecException(msg, var5);
    }
  }

  public int importDataToMySql(String loadDataSql, byte[] data) throws DataBaseOperateException {
    return this.importDataToMySql(loadDataSql, (InputStream) (new ByteArrayInputStream(data)));
  }

  public int importDataToMySql(String loadDataSql, InputStream dataStream)
      throws DataBaseOperateException {
    if (this.dataSource == null) {
      throw new IllegalArgumentException("DataSource must not be null");
    } else if (dataStream == null) {
      log.warn("InputStream is null ,No data is imported");
      return 0;
    } else {
      int result = 0;
      Connection con = DataSourceUtils.getConnection(this.getDataSource());

      try {
        PreparedStatement statement = con.prepareStatement(loadDataSql);
        if (statement.isWrapperFor(Statement.class)) {
          com.mysql.jdbc.PreparedStatement mysqlStatement = (com.mysql.jdbc.PreparedStatement) statement
              .unwrap(com.mysql.jdbc.PreparedStatement.class);
          mysqlStatement.setLocalInfileInputStream(dataStream);
          result = mysqlStatement.executeUpdate();
        }
      } catch (SQLException var10) {
        log.error("Import data to mysql, sql:[{}], Exception:{}", loadDataSql,
            ExceptionUtil.getAsString(var10));
        DataSourceUtils.releaseConnection(con, this.getDataSource());
        con = null;
        throw new DataBaseOperateException(var10.getMessage() + ", sql[" + loadDataSql + "]",
            var10);
      } finally {
        DataSourceUtils.releaseConnection(con, this.getDataSource());
      }

      return result;
    }
  }
}