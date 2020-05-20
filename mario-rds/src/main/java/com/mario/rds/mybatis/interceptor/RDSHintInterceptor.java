package com.mario.rds.mybatis.interceptor;

import com.mario.rds.RDS;
import com.mario.rds.mybatis.PluginUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({@Signature(
    method = "prepare",
    type = StatementHandler.class,
    args = {Connection.class, Integer.class}
)})
public class RDSHintInterceptor implements Interceptor {

  private static final Logger log = LoggerFactory.getLogger(RDSHintInterceptor.class);
  static Field sqlField = null;

  public RDSHintInterceptor() {
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler statementHandler = (StatementHandler) PluginUtils
        .realTarget(invocation.getTarget());
    MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
    MappedStatement mappedStatement = (MappedStatement) metaObject
        .getValue("delegate.mappedStatement");
    if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
      return invocation.proceed();
    } else {
      BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
      String mSql = RDS.getCurrentHintSql() + boundSql.getSql();
      if (sqlField == null) {
        sqlField = boundSql.getClass().getDeclaredField("sql");
        sqlField.setAccessible(true);
      }

      sqlField.set(boundSql, mSql);
      return invocation.proceed();
    }
  }

  @Override
  public Object plugin(Object o) {
    if (o instanceof StatementHandler) {
      byte hintFlag = RDS.getHintFlag();
      if (RDS.isHintMaster(hintFlag) || RDS.isHintSlave(hintFlag)) {
        return Plugin.wrap(o, this);
      }
    }

    return o;
  }

  @Override
  public void setProperties(Properties properties) {
  }
}