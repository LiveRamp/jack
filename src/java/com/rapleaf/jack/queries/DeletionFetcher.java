package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import com.rapleaf.jack.BaseDatabaseConnection;

public class DeletionFetcher extends BaseFetcher {

  public static Deletions getDeletionResults(PreparedStatement preparedStatement, BaseDatabaseConnection dbConnection) throws SQLException {
    try {
      preparedStatement.execute();
      int updatedRowCount = preparedStatement.getUpdateCount();
      return new Deletions(updatedRowCount);
    } catch (SQLRecoverableException e) {
      dbConnection.resetConnection();
      throw e;
    } finally {
      closeQuery(null, preparedStatement, dbConnection);
    }
  }

}
