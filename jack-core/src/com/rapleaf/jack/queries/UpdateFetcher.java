package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import com.rapleaf.jack.BaseDatabaseConnection;

public class UpdateFetcher extends BaseFetcher {

  public static Updates getUpdateResults(PreparedStatement preparedStatement, BaseDatabaseConnection dbConnection) throws SQLException {
    try {
      preparedStatement.execute();
      // PreparedStatement#getUpdateCount returns the number of matched rows by default;
      // it returns the number of changed rows only when useAffectedRows=true is set in the connection string
      // https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html
      int matchedRowCount = preparedStatement.getUpdateCount();
      return new Updates(matchedRowCount);
    } catch (SQLRecoverableException e) {
      dbConnection.resetConnection();
      throw e;
    } finally {
      closeQuery(null, preparedStatement, dbConnection);
    }
  }

}
