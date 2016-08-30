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
  private Optional<String> username;
  private Optional<String> password;

  public DatabaseConnectionConfiguration(String adapter, String host, String dbName, Optional<Integer> port, Optional<Boolean> parrallelTest, Optional<String> username, Optional<String> password) {
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

    String adapter = load("adapter", db_info, "adapter", "database",
        envVar("JACK_DB_ADAPTER", dbname_key), prop("jack.db.adapter", dbname_key), new StringIdentity());

    String host = load("host", db_info, "host", "database",
        envVar("JACK_DB_HOST", dbname_key), prop("jack.db.host", dbname_key), new StringIdentity());

    String dbName = load("database name", db_info, "database", "database",
        envVar("JACK_DB_NAME", dbname_key), prop("jack.db.name", dbname_key), new StringIdentity());

    Optional<Integer> port = loadOpt(db_info, "port",
        envVar("JACK_DB_PORT", dbname_key), prop("jack.db.port", dbname_key), new ToInteger());

    Optional<Boolean> parallelTesting = loadOpt(env_info, "enable_parallel_tests",
        envVar("JACK_DB_PARALLEL_TESTS", dbname_key), prop("jack.db.parallel.test", dbname_key), new ToBoolean());

    Optional<String> username = loadOpt(db_info, "username",
        envVar("JACK_DB_USERNAME", dbname_key), prop("jack.db.username", dbname_key), new StringIdentity());

    Optional<String> password = loadOpt(db_info, "password",
        envVar("JACK_DB_PASSWORD", dbname_key), prop("jack.db.password", dbname_key), new StringIdentity());

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
    return parrallelTest.isPresent() ? parrallelTest.get() : false;
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
