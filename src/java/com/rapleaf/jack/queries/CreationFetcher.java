package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class CreationFetcher {
  private static final Logger LOG = LoggerFactory.getLogger(CreationFetcher.class);

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

  private static void closeQuery(ResultSet resultSet, PreparedStatement preparedStatement, BaseDatabaseConnection dbConnection) {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
      preparedStatement.close();
    } catch (SQLRecoverableException e) {
      LOG.error(e.toString());
      dbConnection.resetConnection();
    } catch (SQLException e) {
      LOG.error(e.toString());
    }
  }

}
