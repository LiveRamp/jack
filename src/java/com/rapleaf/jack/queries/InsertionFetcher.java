package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import com.rapleaf.jack.BaseDatabaseConnection;

public class InsertionFetcher extends BaseFetcher {

  public static long getCreationResults(PreparedStatement preparedStatement, BaseDatabaseConnection dbConnection) throws SQLException {
    ResultSet resultSet = null;

    try {
      preparedStatement.execute();
      resultSet = preparedStatement.getGeneratedKeys();
      resultSet.next();
      return resultSet.getLong(1);
    } catch (SQLRecoverableException e) {
      dbConnection.resetConnection();
      throw e;
    } finally {
      closeQuery(resultSet, preparedStatement, dbConnection);
    }
  }

}
