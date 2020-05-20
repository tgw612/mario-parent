package com.mario.mysql.jdbc;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class INamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

  public INamedParameterJdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }

  public INamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
    super(classicJdbcTemplate);
  }

  public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
      throws DataAccessException {
    List<T> results = this.getJdbcOperations()
        .query(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
    return IDataAccessUtils.requiredSingleResult(results);
  }
}
