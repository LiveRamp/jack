package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import com.rapleaf.jack.BaseDatabaseConnection;

public abstract class BaseExecution {
  protected static int MAX_CONNECTION_RETRIES = 1;

  final BaseDatabaseConnection dbConnection;

  BaseExecution(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
  }

  protected String getSqlStatement() throws IOException {
    return this.getPreparedStatement().toString();
  }

  protected PreparedStatement getPreparedStatement() throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(getQueryStatement());
    setStatementParameters(preparedStatement, getParameters());
    return preparedStatement;
  }

  private void setStatementParameters(PreparedStatement preparedStatement, Collection<Object> parameters) throws IOException {
    int index = 0;
    for (Object parameter : parameters) {
      if (parameter == null) {
        continue;
      }
      try {
        preparedStatement.setObject(++index, parameter);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  protected abstract String getQueryStatement();

  protected abstract Collection<Object> getParameters();

  static String getClauseFromColumns(Collection<Column> columns, String initialKeyword, String separator, String terminalKeyword) {
    if (columns.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<Column> it = columns.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlKeyword());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(terminalKeyword).toString();
  }

  static <T extends QueryCondition> String getClauseFromQueryConditions(Collection<T> conditions, String initialKeyword, String separator, String terminalKeyword) {
    if (conditions.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<T> it = conditions.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlStatement());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(terminalKeyword).toString();
  }

}
