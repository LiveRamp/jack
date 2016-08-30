package com.rapleaf.jack;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.jvyaml.YAML;

public class DatabaseConnectionConfiguration {

  private String adapter;
  private String host;
  private String dbName;
  private Optional<Integer> port;
  private Optional<Boolean> parrallelTest;
  private String username;
  private String password;

  public DatabaseConnectionConfiguration(String adapter, String host, String dbName, Optional<Integer> port, Optional<Boolean> parrallelTest, String username, String password) {
    this.adapter = adapter;
    this.host = host;
    this.dbName = dbName;
    this.port = port;
    this.parrallelTest = parrallelTest;
    this.username = username;
    this.password = password;
  }

  public static DatabaseConnectionConfiguration loadFromEnvironment(String dbname_key) {
    Map<String, Object> env_info;
    Map<String, Object> db_info;
    // load database info from config folder
    try {
      env_info = (Map<String, Object>)YAML.load(new FileReader("config/environment.yml"));
    } catch (FileNotFoundException e) {
      env_info = Maps.newHashMap();
    }
    String db_info_name = (String)env_info.get(dbname_key);
    try {
      db_info = (Map)((Map)YAML.load(new FileReader("config/database.yml"))).get(db_info_name);
    } catch (FileNotFoundException e) {
      db_info = Maps.newHashMap();
    }

    String adapter = load("adapter", db_info, "adapter", "database", "JACK_DB_ADAPTER", "jack.db.adapter", new StringIdentity());
    String host = load("host", db_info, "host", "database", "JACK_DB_HOST", "jack.db.host", new StringIdentity());
    String dbName = load("database name", db_info, "database", "database", "JACK_DB_NAME", "jack.db.name", new StringIdentity());
    Optional<Integer> port = loadOpt("port", db_info, "port", "database", "JACK_DB_PORT", "jack.db.port", new ToInteger());
    Optional<Boolean> parallelTesting = loadOpt("parrallel testing", env_info, "enable_parallel_tests", "environment",
        "JACK_DB_PARALLEL_TESTS", "jack.db.parallel.test", new ToBoolean());

    String username = load("username", db_info, "username", "database", "JACK_DB_USERNAME", "jack.db.username", new StringIdentity());
    String password = load("password", db_info, "password", "database", "JACK_DB_PASSWORD", "jack.db.password", new StringIdentity());

    return new DatabaseConnectionConfiguration(adapter, host, dbName, port, parallelTesting, username, password);
  }

  private static <T> T load(
      String readableName,
      Map<String, Object> map,
      String mapKey,
      String mapYmlFile,
      String envVar,
      String javaProp,
      Function<String, T> fromString) {

    Optional<T> result = loadOpt(readableName, map, mapKey, mapYmlFile, envVar, javaProp, fromString);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new RuntimeException("Unable to find required configuration " + readableName + ". Please set using one of:\n" +
          "Environment Variable: " + envVar + "\n" +
          "Java System Property: " + javaProp + "\n" +
          "Entry in config/environment.yml or config/database.yml: " + mapKey);
    }
  }

  private static <T> Optional<T> loadOpt(
      String readableName,
      Map<String, Object> map,
      String mapKey,
      String mapYmlFile,
      String envVar,
      String javaProp,
      Function<String, T> fromString) {
    if (System.getenv(envVar) != null) {
      return Optional.fromNullable(fromString.apply(System.getenv(envVar)));
    }
    if (System.getProperty(javaProp) != null) {
      return Optional.fromNullable(fromString.apply(System.getProperty(javaProp)));
    }
    if (map.containsKey(mapKey)) {
      return Optional.fromNullable((T)map.get(mapKey));
    }
    return Optional.absent();
  }


  public String getAdapter() {
    return adapter;
  }

  public String getHost() {
    return host;
  }

  public Optional<Integer> getPort() {
    return port;
  }

  public String getDatabaseName() {
    return dbName;
  }

  public Boolean enableParallelTests() {
    return parrallelTest.isPresent() ? parrallelTest.get() : false;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  private static class StringIdentity implements Function<String, String> {
    public String apply(String s) {
      return s;
    }
  }

  private static class ToInteger implements Function<String, Integer> {
    public Integer apply(String s) {
      return Integer.parseInt(s);
    }
  }

  private static class ToBoolean implements Function<String, Boolean> {
    public Boolean apply(String s) {
      return Boolean.parseBoolean(s);
    }
  }
}
