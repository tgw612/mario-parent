package com.mario.mysql.jdbc;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {

  public JdbcTemplate() {
  }

  public JdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }

  public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
    super(dataSource, lazyInit);
  }

  public void setEnableJdbcTemplateUtil(boolean enable) {
    if (enable) {
      JdbcTemplateUtil.jdbcTemplate(this);
    }

  }

  @Override
  public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper)
      throws DataAccessException {
    List<T> results = (List) this.query(sql, args, new RowMapperResultSetExtractor(rowMapper, 1));
    return IDataAccessUtils.requiredSingleResult(results);
  }
}