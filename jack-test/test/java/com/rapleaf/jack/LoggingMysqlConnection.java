package com.rapleaf.jack;

import java.sql.PreparedStatement;
import java.util.List;

import com.google.common.collect.Lists;

public class LoggingMysqlConnection extends MysqlDatabaseConnection {

  private final List<String> preparedStatements = Lists.newArrayList();

  public LoggingMysqlConnection(String dbNameKey) {
    super(dbNameKey);
  }

  public PreparedStatement getPreparedStatement(String statement) {
    preparedStatements.add(statement);
    return super.getPreparedStatement(statement);
  }

  public PreparedStatement getPreparedStatement(String statement, int options) {
    preparedStatements.add(statement);
    return super.getPreparedStatement(statement, options);
  }

  public List<String> getPreparedStatements() {
    return preparedStatements;
  }

  public void clearPreparedStatements() {
    preparedStatements.clear();
  }

}
