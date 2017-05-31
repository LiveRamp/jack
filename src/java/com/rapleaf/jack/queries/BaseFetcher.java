package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class BaseFetcher {
  private static final Logger LOG = LoggerFactory.getLogger(BaseFetcher.class);

  static void closeQuery(ResultSet resultSet, PreparedStatement preparedStatement, BaseDatabaseConnection dbConnection) {
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
