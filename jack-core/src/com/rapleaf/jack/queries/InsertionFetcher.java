package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;

import com.google.common.collect.Lists;

import com.rapleaf.jack.BaseDatabaseConnection;

public class InsertionFetcher extends BaseFetcher {

  public static Insertions getCreationResults(PreparedStatement preparedStatement, int rowCount, BaseDatabaseConnection dbConnection) throws SQLException {
    ResultSet resultSet = null;
    List<Long> newIds = Lists.newArrayListWithCapacity(rowCount);

    try {
      preparedStatement.execute();
      resultSet = preparedStatement.getGeneratedKeys();
      for (int i = 0; i < rowCount; ++i) {
        resultSet.next();
        newIds.add(resultSet.getLong(1));
      }
      return new Insertions(newIds);
    } catch (SQLRecoverableException e) {
      dbConnection.resetConnection();
      throw e;
    } finally {
      closeQuery(resultSet, preparedStatement, dbConnection);
    }
  }

}
