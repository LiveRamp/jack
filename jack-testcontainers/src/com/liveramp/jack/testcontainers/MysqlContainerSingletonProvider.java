package com.liveramp.jack.testcontainers;

import java.io.IOException;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

public class MysqlContainerSingletonProvider extends LazyLoadingSingletonFactory<MySQLContainer> {

  private static final String CONTAINER_SQL_DUMP_PATH = "/etc/sql.dump";
  public static final String DEFAULT_MYSQL_DOCKER_IMAGE = "mysql:5.5";

  private final String dbName;
  private final String labelledMysqlDockerImage;

  public MysqlContainerSingletonProvider(String dbName) {
    this(dbName, DEFAULT_MYSQL_DOCKER_IMAGE);
  }

  public MysqlContainerSingletonProvider(String dbName, String labelledMysqlDockerImage) {
    this.dbName = dbName;
    this.labelledMysqlDockerImage = labelledMysqlDockerImage;
  }

  @Override
  protected MySQLContainer create() {
    String resourcePath = dbName + ".dump";

    MySQLContainer container = new MySQLContainer(labelledMysqlDockerImage)
        .withDatabaseName(dbName)
        .withUsername(MysqlContainerConstants.USERNAME)
        .withPassword(MysqlContainerConstants.PASSWORD);

    container.start();

    try {
      container.copyFileToContainer(MountableFile.forClasspathResource(resourcePath), CONTAINER_SQL_DUMP_PATH);

      // execInContainer uses Docker's EXEC (which isn't bash), which means that something like mysql < dump.sql is not possible
      container.execInContainer("mysql", "-u" + MysqlContainerConstants.USERNAME, "-p" + MysqlContainerConstants.PASSWORD, "-e", "SET autocommit=0 ; use " + dbName + "; source " + CONTAINER_SQL_DUMP_PATH + " ; COMMIT ;");
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    return container;
  }

}
