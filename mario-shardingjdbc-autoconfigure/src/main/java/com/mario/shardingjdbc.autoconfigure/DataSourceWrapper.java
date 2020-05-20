package com.mario.shardingjdbc.autoconfigure;

import javax.sql.DataSource;

@FunctionalInterface
public interface DataSourceWrapper {

  DataSource wrapDataSource(DataSource var1) throws Exception;
}
