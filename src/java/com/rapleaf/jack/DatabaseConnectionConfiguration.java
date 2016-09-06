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
  private Optional<Boolean> parallelTest;
  private Optional<String> username;
  private Optional<String> password;

  public DatabaseConnectionConfiguration(String adapter, String host, String dbName, Optional<Integer> port, Optional<Boolean> parallelTest, Optional<String> username, Optional<String> password) {
    this.adapter = adapter;
    this.host = host;
    this.dbName = dbName;
    this.port = port;
    this.parallelTest = parallelTest;
    this.username = username;
    this.password = password;
  }

  public static DatabaseConnectionConfiguration loadFromEnvironment(String dbNameKey) {
    Map<String, Object> envInfo;
    Map<String, Object> dbInfo;
    // load database info from config folder
    try {
      envInfo = (Map<String, Object>)YAML.load(new FileReader("config/environment.yml"));
    } catch (FileNotFoundException e) {
      envInfo = Maps.newHashMap();
    }
    String db_info_name = (String)envInfo.get(dbNameKey);
    try {
      dbInfo = (Map)((Map)YAML.load(new FileReader("config/database.yml"))).get(db_info_name);
    } catch (FileNotFoundException e) {
      dbInfo = Maps.newHashMap();
    }

    String adapter = load("adapter", dbInfo, "adapter", "database",
        envVar("JACK_DB_ADAPTER", dbNameKey), prop("jack.db.adapter", dbNameKey), new StringIdentity());

    String host = load("host", dbInfo, "host", "database",
        envVar("JACK_DB_HOST", dbNameKey), prop("jack.db.host", dbNameKey), new StringIdentity());

    String dbName = load("database name", dbInfo, "database", "database",
        envVar("JACK_DB_NAME", dbNameKey), prop("jack.db.name", dbNameKey), new StringIdentity());

    Optional<Integer> port = loadOpt(dbInfo, "port",
        envVar("JACK_DB_PORT", dbNameKey), prop("jack.db.port", dbNameKey), new ToInteger());

    Optional<Boolean> parallelTesting = loadOpt(envInfo, "enable_parallel_tests",
        envVar("JACK_DB_PARALLEL_TESTS", dbNameKey), prop("jack.db.parallel.test", dbNameKey), new ToBoolean());

    Optional<String> username = loadOpt(dbInfo, "username",
        envVar("JACK_DB_USERNAME", dbNameKey), prop("jack.db.username", dbNameKey), new StringIdentity());

    Optional<String> password = loadOpt(dbInfo, "password",
        envVar("JACK_DB_PASSWORD", dbNameKey), prop("jack.db.password", dbNameKey), new StringIdentity());

    return new DatabaseConnectionConfiguration(adapter, host, dbName, port, parallelTesting, username, password);
  }

  private static String envVar(String baseEnvVar, String dbname_key) {
    return baseEnvVar + "_" + dbname_key.toUpperCase();
  }

  private static String prop(String baseProp, String dbname_key) {
    return baseProp + "." + dbname_key;
  }

  private static <T> T load(
      String readableName,
      Map<String, Object> map,
      String mapKey,
      String mapYmlFile,
      String envVar,
      String javaProp,
      Function<String, T> fromString) {

    Optional<T> result = loadOpt(map, mapKey, envVar, javaProp, fromString);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new RuntimeException("Unable to find required configuration " + readableName + ". Please set using one of:\n" +
          "Environment Variable: " + envVar + "\n" +
          "Java System Property: " + javaProp + "\n" +
          "Entry in config/" + mapYmlFile + ".yml: " + mapKey);
    }
  }

  private static <T> Optional<T> loadOpt(
      Map<String, Object> map,
      String mapKey,
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
    return parallelTest.isPresent() ? parallelTest.get() : false;
  }

  public Optional<String> getUsername() {
    return username;
  }

  public Optional<String> getPassword() {
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
