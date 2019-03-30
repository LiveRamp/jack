package com.rapleaf.jack.mariadb;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class MariaServerSingletonProvider extends LazyLoadingSingletonFactory<DB> {
  public static final MariaServerSingletonProvider INSTANCE = new MariaServerSingletonProvider();

  private MariaServerSingletonProvider() {
    // no-op, only used to force people to use a single instance per JVM
  }

  private static int getOpenPort() throws IOException {
    ServerSocket socket = new ServerSocket(0);
    int port = socket.getLocalPort();
    socket.close();
    return port;
  }

  @Override
  public DB create() {
    try {
      int port = getOpenPort();
      String uuid = UUID.randomUUID().toString();
      String basePath = "/tmp/MariaDB4j/";
      String tempBaseDir = basePath + "base/" + uuid;
      String tempDataDir = basePath + "data/" + uuid;

      DBConfiguration config = DBConfigurationBuilder.newBuilder()
          .setPort(port)
          .setBaseDir(tempBaseDir)
          .setDataDir(tempDataDir)
          .build();

      DB database = DB.newEmbeddedDB(config);
      database.start();

      return database;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
