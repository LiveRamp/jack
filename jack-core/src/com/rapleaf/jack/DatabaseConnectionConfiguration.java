package com.rapleaf.jack;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.jvyaml.YAML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnectionConfiguration {

  private final static Logger LOG = LoggerFactory.getLogger(DatabaseConnectionConfiguration.class);

  public static final String ADAPTER_PROP_PREFIX = "jack.db.adapter";
  public static final String HOST_PROP_PREFIX = "jack.db.host";
  public static final String NAME_PROP_PREFIX = "jack.db.name";
  public static final String PORT_PROP_PREFIX = "jack.db.port";
  public static final String PARALLEL_TEST_PROP_PREFIX = "jack.db.parallel.test";
  public static final String USERNAME_PROP_PREFIX = "jack.db.username";
  public static final String PASSWORD_PROP_PREFIX = "jack.db.password";
  public static final String DATABASE_YML_PROP = "jack.db.database.yml";
  public static final String ENVIRONMENT_YML_PROP = "jack.db.environment.yml";
  public static final String DATABASE_PATH_PROP = "jack.db.database.config.path";
  public static final String ENVIRONMENT_PATH_PROP = "jack.db.environment.config.path";
  public static final String CONNECTION_MAX_RETRIES = "jack.db.connection_max_retries";
  public static final String CONNECTION_VALIDATION_TIMEOUT = "jack.db.connection_validation_timeout";


  private String adapter;
  private String host;
  private String dbName;
  private Optional<Integer> port;
  private Optional<Boolean> parallelTest;
  private Optional<String> username;
  private Optional<String> password;
  private Optional<Integer> connectionMaxRetries;
  private Optional<Integer> connectionValidationTimeout;

  public DatabaseConnectionConfiguration(String adapter, String host, String dbName, Optional<Integer> port, Optional<Boolean> parallelTest, Optional<String> username, Optional<String> password) {
    this(
        adapter,
        host,
        dbName,
        port,
        parallelTest,
        username,
        password,
        Optional.absent(),
        Optional.absent()
    );
  }

  public DatabaseConnectionConfiguration(String adapter, String host, String dbName, Optional<Integer> port, Optional<Boolean> parallelTest, Optional<String> username, Optional<String> password, Optional<Integer> connectionMaxRetries, Optional<Integer> connectionValidationTimeout) {
    this.adapter = adapter;
    this.host = host;
    this.dbName = dbName;
    this.port = port;
    this.parallelTest = parallelTest;
    this.username = username;
    this.password = password;
    this.connectionMaxRetries = connectionMaxRetries;
    this.connectionValidationTimeout = connectionValidationTimeout;
  }

  public static DatabaseConnectionConfiguration loadFromEnvironment(String dbNameKey) {
    Map<String, Object> envInfo;
    Map<String, Object> dbInfo;
    // load database info from some file - first check env, then props, then default location
    envInfo = fetchInfoMap(
        "environment",
        new EnvVarProvider(envVar(ENVIRONMENT_YML_PROP)),
        new PropertyProvider(ENVIRONMENT_YML_PROP),
        new FileReaderProvider(System.getenv(envVar(ENVIRONMENT_PATH_PROP))),
        new FileReaderProvider(System.getProperty(ENVIRONMENT_PATH_PROP)),
        new FileReaderProvider("config/environment.yml"),
        new FileReaderProvider("environment.yml"));

    String db_info_name = (String)envInfo.get(dbNameKey);

    dbInfo = (Map<String, Object>)fetchInfoMap(
        "database",
        new EnvVarProvider(envVar(DATABASE_YML_PROP)),
        new PropertyProvider(DATABASE_YML_PROP),
        new FileReaderProvider(System.getenv(envVar(DATABASE_PATH_PROP))),
        new FileReaderProvider(System.getProperty(DATABASE_PATH_PROP)),
        new FileReaderProvider("config/database.yml"),
        new FileReaderProvider("database.yml")).get(db_info_name);

    String adapter = load("adapter", dbInfo, "adapter", "database",
        envVar(ADAPTER_PROP_PREFIX, dbNameKey), prop(ADAPTER_PROP_PREFIX, dbNameKey), new StringIdentity());

    String host = load("host", dbInfo, "host", "database",
        envVar(HOST_PROP_PREFIX, dbNameKey), prop(HOST_PROP_PREFIX, dbNameKey), new StringIdentity());

    String dbName = load("database name", dbInfo, "database", "database",
        envVar(NAME_PROP_PREFIX, dbNameKey), prop(NAME_PROP_PREFIX, dbNameKey), new StringIdentity());

    Optional<Integer> port = loadOpt(dbInfo, "port",
        envVar(PORT_PROP_PREFIX, dbNameKey), prop(PORT_PROP_PREFIX, dbNameKey), new ToInteger());

    Optional<Boolean> parallelTesting = loadOpt(envInfo, "enable_parallel_tests",
        envVar(PARALLEL_TEST_PROP_PREFIX, dbNameKey), prop(PARALLEL_TEST_PROP_PREFIX, dbNameKey), new ToBoolean());

    Optional<String> username = loadOpt(dbInfo, "username",
        envVar(USERNAME_PROP_PREFIX, dbNameKey), prop(USERNAME_PROP_PREFIX, dbNameKey), new StringIdentity());

    Optional<String> password = loadOpt(dbInfo, "password",
        envVar(PASSWORD_PROP_PREFIX, dbNameKey), prop(PASSWORD_PROP_PREFIX, dbNameKey), new StringIdentity());

    Optional<Long> connectionMaxRetriesLong = loadOpt(dbInfo, "connection_max_retries",
        envVar(CONNECTION_MAX_RETRIES, dbNameKey), prop(CONNECTION_MAX_RETRIES, dbNameKey), new ToLong());

    Optional<Integer> connectionMaxRetries = Optional.absent();
    if (connectionMaxRetriesLong.isPresent()) {
      /**
       * This manual transformation is necessary because the underlying type parsed by
       * {@link #loadOpt(Map, String, String, String, Function)} is actually a Long. This is a
       * result of the YAML parser loading all numbers as Longs, not Integers. The only reason we
       * don't see this issue with the port config as well is because we never actually treat it
       * as an int later on. Without doing this, we'll get a {@link ClassCastException} when we
       * attempt to unwrap this into an int later on.
       */
      connectionMaxRetries = Optional.of(connectionMaxRetriesLong.get().intValue());
    }

    Optional<Long> connectionValidationTimeoutLong = loadOpt(dbInfo,
        "connection_validation_timeout",
        envVar(CONNECTION_VALIDATION_TIMEOUT, dbNameKey),
        prop(CONNECTION_VALIDATION_TIMEOUT, dbNameKey),
        new ToLong()
    );

    Optional<Integer> connectionValidationTimeout = Optional.absent();
    if (connectionValidationTimeoutLong.isPresent()) {
      connectionValidationTimeout = Optional.of(connectionValidationTimeoutLong.get().intValue());
    }

    return new DatabaseConnectionConfiguration(adapter, host, dbName, port, parallelTesting, username, password, connectionMaxRetries, connectionValidationTimeout);
  }

  private static Map<String, Object> fetchInfoMap(String configName, ReaderProvider... readers) {
    for (ReaderProvider reader : readers) {
      try {
        Optional<Reader> readerOptional = reader.get();
        if (readerOptional.isPresent()) {
          return (Map<String, Object>)YAML.load(readerOptional.get());
        }
      } catch (Exception e) {
        //move to next reader
      }
    }
    LOG.error("no yaml found for config: " + configName);
    return Maps.newHashMap();
  }

  private interface ReaderProvider {
    Optional<Reader> get() throws Exception;
  }

  public static String envVar(String propertyPrefix, String dbNameKey) {
    return envVar(propertyPrefix) + "_" + dbNameKey.toUpperCase();
  }

  public static String envVar(String property) {
    return property.replace('.', '_').toUpperCase();
  }

  private static String prop(String baseProp, String dbNameKey) {
    return baseProp + "." + dbNameKey;
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
      String foundKeys;
      if (map == null) {
        foundKeys = "No keys found";
      } else {
        foundKeys = map.keySet().toString();
      }
      throw new RuntimeException(
          "Unable to find required configuration " + readableName + ". Please set using one of:\n" +
              "Environment Variable: " + envVar + "\n" +
              "Java System Property: " + javaProp + "\n" +
              "Entry in config/" + mapYmlFile + ".yml: " + mapKey + "\n" +
              "Found following keys: " + foundKeys);
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
    if (map != null && map.containsKey(mapKey)) {
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

  public Optional<Integer> getConnectionMaxRetries() {
    return connectionMaxRetries;
  }

  public Optional<Integer> getConnectionValidationTimeout() {
    return connectionValidationTimeout;
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

  private static class ToLong implements Function<String, Long> {
    public Long apply(String s) {
      return Long.parseLong(s);
    }
  }

  private static class ToBoolean implements Function<String, Boolean> {
    public Boolean apply(String s) {
      return Boolean.parseBoolean(s);
    }
  }

  private static class FileReaderProvider implements ReaderProvider {

    private String file;

    public FileReaderProvider(String file) {
      this.file = file;
    }

    @Override
    public Optional<Reader> get() throws Exception {
      if (file != null) {
        return Optional.of(new FileReader(file));
      } else {
        return Optional.absent();
      }
    }
  }

  private static class EnvVarProvider implements ReaderProvider {

    private String envVar;

    public EnvVarProvider(String envVar) {
      this.envVar = envVar;
    }

    @Override
    public Optional<Reader> get() throws Exception {
      if (System.getenv(envVar) != null) {
        return Optional.of(new StringReader(System.getenv(envVar)));
      } else {
        return Optional.absent();
      }
    }
  }

  private static class PropertyProvider implements ReaderProvider {

    private String property;

    public PropertyProvider(String property) {
      this.property = property;
    }

    @Override
    public Optional<Reader> get() throws Exception {
      if (System.getProperty(property) != null) {
        return Optional.of(new StringReader(System.getProperty(property)));
      } else {
        return Optional.absent();
      }
    }
  }
}
