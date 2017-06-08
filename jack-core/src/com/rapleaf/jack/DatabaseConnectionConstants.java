package com.rapleaf.jack;

import java.time.Duration;

public class DatabaseConnectionConstants {
  public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
  public static final String POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver";
  public static final String REDSHIFT_JDBC_DRIVER = "com.amazon.redshift.jdbc41.Driver";

  public static final long DEFAULT_EXPIRATION = Duration.ofHours(4).toMillis(); // 4 hours
}
