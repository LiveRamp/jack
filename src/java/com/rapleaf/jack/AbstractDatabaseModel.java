//
// Copyright 2011 Rapleaf
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.rapleaf.jack;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.exception.JackException;
import com.rapleaf.jack.exception.StaleModelException;
import com.rapleaf.jack.exception.TooManyRowsUpdatedException;
import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.ModelDelete;
import com.rapleaf.jack.queries.ModelQuery;
import com.rapleaf.jack.queries.WhereClause;

public abstract class AbstractDatabaseModel<T extends ModelWithId<T, ? extends GenericDatabases>> implements
    IModelPersistence<T> {

  public static final String DEFAULT_LOCK_FIELD_NAME = "lock_version";
  private static Logger LOG = LoggerFactory.getLogger(AbstractDatabaseModel.class);

  protected static final int MAX_CONNECTION_RETRIES = 1;
  private final String idQuoteString;
  private final String setFieldsPrepStatementSection;
  private final String lockFieldName;

  protected static interface AttrSetter {
    public void set(PreparedStatement stmt) throws SQLException;
  }

  private final BaseDatabaseConnection conn;
  private final String tableName;

  private final List<String> fieldNames;

  protected final Map<Long, T> cachedById = new HashMap<Long, T>();
  protected final Map<String, Map<Long, List<T>>> cachedByForeignKey = new HashMap<String, Map<Long, List<T>>>();

  private boolean useCache = true;

  protected AbstractDatabaseModel(BaseDatabaseConnection conn,
                                  String tableName, List<String> fieldNames) {
    this.conn = conn;
    this.tableName = tableName;
    this.fieldNames = fieldNames;
    try {
      idQuoteString = conn.getConnection().getMetaData().getIdentifierQuoteString();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    setFieldsPrepStatementSection = getSetFieldsPrepStatementSection();
    lockFieldName = DEFAULT_LOCK_FIELD_NAME;
  }

  protected String getInsertStatement(List<String> fieldNames) {
    return String.format("INSERT INTO %s (%s) VALUES(%s);", tableName,
        escapedFieldNames(fieldNames), qmarks(fieldNames.size()));
  }

  protected String getInsertWithIdStatement(List<String> fieldNames) {
    return String.format("INSERT INTO %s (%s , id) VALUES(%s, ?);", tableName,
        escapedFieldNames(fieldNames), qmarks(fieldNames.size()));
  }

  private String getSetFieldsPrepStatementSection() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(", ");
      }

      sb.append(idQuoteString)
          .append(fieldNames.get(i))
          .append(idQuoteString)
          .append(" = ?");
    }
    return sb.toString();
  }

  private String getUpdateOnInsertPrepStatementSection() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append(idQuoteString)
          .append(fieldNames.get(i))
          .append(idQuoteString)
          .append(" = VALUES(")
          .append(idQuoteString)
          .append(fieldNames.get(i))
          .append(idQuoteString)
          .append(")");
    }
    return sb.toString();
  }

  protected BaseDatabaseConnection getConn() {
    return conn;
  }

  protected T instanceFromResultSet(ResultSet rs) throws SQLException {
    return instanceFromResultSet(rs, null);
  }

  protected abstract T instanceFromResultSet(ResultSet rs, Set<Enum> selectedFields) throws SQLException;

  protected long realCreate(AttrSetter attrSetter, String insertStatement)
      throws IOException {
    int retryCount = 0;

    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;
    while (true) {
      try {
        stmt = conn.getPreparedStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);
        attrSetter.set(stmt);
        stmt.execute();
        generatedKeys = stmt.getGeneratedKeys();
        generatedKeys.next();
        long newId = generatedKeys.getLong(1);
        return newId;
      } catch (SQLRecoverableException e) {
        conn.resetConnection(e);
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      } finally {
        closeQuery(generatedKeys, stmt);
      }
    }
  }

  @Override
  public abstract T create(Map<Enum, Object> fieldsMap) throws IOException;

  private String escapedFieldNames(List<String> fieldNames) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append(idQuoteString)
          .append(fieldNames.get(i))
          .append(idQuoteString);
    }
    return sb.toString();
  }

  @Override
  public T find(long id) throws IOException {
    if (cachedById.containsKey(id) && useCache) {
      return cachedById.get(id);
    }
    int retryCount = 0;

    PreparedStatement stmt = null;
    ResultSet rs = null;
    T model = null;
    while (true) {
      try {
        stmt = conn.getPreparedStatement("SELECT * FROM "
            + tableName + " WHERE id=" + id);
        rs = stmt.executeQuery();
        model = rs.next() ? instanceFromResultSet(rs) : null;
        if (model != null) {
          model.setCreated(true);
        }
        break;
      } catch (SQLRecoverableException e) {
        conn.resetConnection(e);
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      } finally {
        closeQuery(rs, stmt);
      }
    }
    if (useCache) {
      cachedById.put(id, model);
    }
    return model;
  }

  @Override
  public List<T> find(Set<Long> ids) throws IOException {
    List<T> foundList = new ArrayList<T>();
    Set<Long> notCachedIds = new HashSet<Long>();
    if (useCache) {
      for (Long id : ids) {
        if (cachedById.containsKey(id)) {
          T model = cachedById.get(id);
          foundList.add(model);
        } else {
          notCachedIds.add(id);
        }
      }
    } else {
      notCachedIds = ids;
    }
    if (!notCachedIds.isEmpty()) {
      executeQuery(foundList, "SELECT * FROM " + tableName + " WHERE " + getIdSetCondition(notCachedIds));
    }
    return foundList;
  }

  public List<T> findWithOrder(Set<Long> ids, ModelQuery query) throws IOException {
    List<T> foundList = new ArrayList<T>();
    if (!ids.isEmpty()) {
      String statement = query.getSelectClause();
      statement += " FROM ";
      statement += tableName;
      statement += " WHERE ";
      statement += getIdSetCondition(ids);
      statement += query.getOrderByClause();
      statement += query.getLimitClause();
      executeQuery(foundList, statement);
    }
    return foundList;
  }

  public List<T> find(ModelQuery query) throws IOException {
    List<T> foundList = new ArrayList<T>();

    if (query.isOnlyIdQuery()) {
      Optional<Set<Long>> ids = query.getIdSet();
      if (ids.isPresent() && !ids.get().isEmpty()) {
        return find(ids.get());
      }
      return foundList;
    }

    String statementString = getPreparedStatementString(query, false);

    int retryCount = 0;
    PreparedStatement preparedStatement;

    while (true) {
      preparedStatement = getPreparedStatement(statementString);
      setStatementParameters(preparedStatement, query.getWhereClause());
      Set<Enum> selectedFields = getSelectedFields(query);

      try {
        executeQuery(foundList, preparedStatement, selectedFields);
        return foundList;
      } catch (SQLRecoverableException e) {
        if (++retryCount > AbstractDatabaseModel.MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
        logRetryAttempt(statementString, e);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  public List<T> findWithOrder(ModelQuery query) throws IOException {
    List<T> foundList = new ArrayList<T>();

    if (query.isOnlyIdQuery()) {
      Optional<Set<Long>> ids = query.getIdSet();
      if (ids.isPresent() && !ids.get().isEmpty()) {
        return findWithOrder(ids.get(), query);
      }
      return foundList;
    }

    String statementString = getPreparedStatementString(query, true);

    int retryCount = 0;
    PreparedStatement preparedStatement;

    while (true) {
      preparedStatement = getPreparedStatement(statementString);
      setStatementParameters(preparedStatement, query.getWhereClause());
      Set<Enum> selectedFields = getSelectedFields(query);

      try {
        executeQuery(foundList, preparedStatement, selectedFields);
        return foundList;
      } catch (SQLRecoverableException e) {
        if (++retryCount > AbstractDatabaseModel.MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
        logRetryAttempt(statementString, e);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  public boolean delete(ModelDelete delete) throws IOException {
    String statementString = delete.getStatement(this.getTableName());
    try (PreparedStatement statement = getPreparedStatement(statementString)) {
      setStatementParameters(statement, delete.getWhereClause());
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      // Delete by query may invalidate the cache
      this.cachedById.clear();
      this.cachedByForeignKey.clear();
    }
    return true;
  }

  private String getPreparedStatementString(ModelQuery query, boolean order) throws IOException {
    String statement = query.getSelectClause();
    statement += " FROM " + getTableName() + " ";
    statement += query.getWhereClause().getSqlString();
    statement += query.getGroupByClause();
    statement += order ? query.getOrderByClause() : "";
    statement += query.getLimitClause();

    return statement;
  }

  private Set<Enum> getSelectedFields(ModelQuery query) throws IOException {
    // Extract the list of selected columns from the list of FieldSelector we have
    Set<Enum> selectedFields = new HashSet<Enum>();
    for (FieldSelector selector : query.getSelectedFields()) {
      selectedFields.add(selector.getField());
    }
    return selectedFields;
  }

  protected String getIdSetCondition(Set<Long> ids) {
    StringBuilder sb = new StringBuilder("id in (");
    Iterator<Long> iter = ids.iterator();
    while (iter.hasNext()) {
      Long obj = iter.next();
      sb.append(obj.toString());
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  protected abstract void setStatementParameters(PreparedStatement statement, WhereClause whereClause) throws IOException;

  protected void executeQuery(Collection<T> foundSet, PreparedStatement stmt) throws SQLException {
    executeQuery(foundSet, stmt, null);
  }

  protected void executeQuery(Collection<T> foundSet, PreparedStatement stmt, Set<Enum> selectedFields) throws SQLException {
    ResultSet rs = null;

    try {
      rs = stmt.executeQuery();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs, selectedFields);
        inst.setCreated(true);
        foundSet.add(inst);
        if (useCache) {
          cachedById.put(inst.getId(), inst);
        }
      }
    } catch (SQLRecoverableException e) {
      conn.resetConnection(e);
      throw e;
    } finally {
      closeQuery(rs, stmt);
    }
  }

  protected void executeQuery(Collection<T> foundSet, String statementString) throws IOException {
    int retryCount = 0;
    PreparedStatement stmt;

    while (true) {
      try {
        stmt = conn.getPreparedStatement(statementString);
        executeQuery(foundSet, stmt);
        break;
      } catch (SQLRecoverableException e) {
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
        logRetryAttempt(statementString, e);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  protected PreparedStatement getPreparedStatement(String statementString) {
    return conn.getPreparedStatement(statementString);
  }

  protected final static Integer getIntOrNull(ResultSet rs, String column)
      throws SQLException {
    Integer value = rs.getInt(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Long getLongOrNull(ResultSet rs, String column)
      throws SQLException {
    Long value = rs.getLong(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Double getDoubleOrNull(ResultSet rs, String column)
      throws SQLException {
    Double value = rs.getDouble(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Boolean getBooleanOrNull(ResultSet rs, String column)
      throws SQLException {
    Boolean value = rs.getBoolean(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Date getDate(ResultSet rs, String column)
      throws SQLException {
    Timestamp timestamp = rs.getTimestamp(column);
    if (timestamp == null) {
      return null;
    }
    return new Date(timestamp.getTime());
  }

  protected final static Long getDateAsLong(ResultSet rs, String column)
      throws SQLException {
    Date date = getDate(rs, column);
    return date == null ? null : date.getTime();
  }

  @Override
  public void clearCacheByForeignKey(String foreignKey, long id) {
    Map<Long, List<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    if (foreignKeyCache != null) {
      foreignKeyCache.remove(id);
    }
  }

  @Override
  public void clearCacheById(long id) throws IOException {
    cachedById.remove(id);
  }

  @Override
  public void clearForeignKeyCache() {
    cachedByForeignKey.clear();
  }

  @Override
  public List<T> findAllByForeignKey(String foreignKey, long id)
      throws IOException {
    Map<Long, List<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    List<T> ret;
    if (foreignKeyCache != null && useCache) {
      ret = foreignKeyCache.get(id);
      if (ret != null) {
        return ret;
      }
    } else if (useCache) {
      foreignKeyCache = new HashMap<Long, List<T>>();
      cachedByForeignKey.put(foreignKey, foreignKeyCache);
    }

    int retryCount = 0;

    PreparedStatement stmt = null;
    ResultSet rs = null;

    while (true) {
      try {
        stmt = conn.getPreparedStatement(String.format(
            "SELECT * FROM %s WHERE %s = %d;", tableName, foreignKey, id));
        rs = stmt.executeQuery();
        ret = new ArrayList<T>();
        while (rs.next()) {
          T inst = instanceFromResultSet(rs);
          inst.setCreated(true);
          if (useCache) {
            if (cachedById.containsKey(inst.getId())) {
              inst = cachedById.get(inst.getId());
            } else {
              cachedById.put(inst.getId(), inst);
            }
          }
          ret.add(inst);
        }
        if (useCache) {
          foreignKeyCache.put(id, ret);
        }
        return ret;
      } catch (SQLRecoverableException e) {
        conn.resetConnection(e);
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      } finally {
        closeQuery(rs, stmt);
      }
    }
  }

  @Override
  public List<T> findAllByForeignKey(String foreignKey, Set<Long> ids)
      throws IOException {
    Map<Long, List<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    List<T> foundList = new ArrayList<T>();
    Set<Long> notCachedIds = new HashSet<Long>();
    if (foreignKeyCache != null && useCache) {
      for (Long id : ids) {
        List<T> results = foreignKeyCache.get(id);
        if (results != null) {
          foundList.addAll(results);
        } else {
          notCachedIds.add(id);
        }
      }
    } else {
      notCachedIds = ids;
      if (useCache) {
        foreignKeyCache = new HashMap<Long, List<T>>();
        cachedByForeignKey.put(foreignKey, foreignKeyCache);
      }
    }

    if (!notCachedIds.isEmpty()) {
      StringBuilder statementString = new StringBuilder();
      statementString.append("SELECT * FROM ");
      statementString.append(tableName);
      statementString.append(" WHERE " + foreignKey + " in (");
      Iterator<Long> iter = notCachedIds.iterator();
      while (iter.hasNext()) {
        Long obj = iter.next();
        statementString.append(obj.toString());
        if (iter.hasNext()) {
          statementString.append(",");
        }
      }
      statementString.append(")");

      int retryCount = 0;

      PreparedStatement stmt = null;
      ResultSet rs = null;

      while (true) {
        try {
          stmt = conn.getPreparedStatement(statementString.toString());
          rs = stmt.executeQuery();
          while (rs.next()) {
            T inst = instanceFromResultSet(rs);
            inst.setCreated(true);
            foundList.add(inst);
            if (useCache) {
              cachedById.put(inst.getId(), inst);
            }
          }
          break;
        } catch (SQLRecoverableException e) {
          conn.resetConnection(e);
          if (++retryCount > MAX_CONNECTION_RETRIES) {
            throw new IOException(e);
          }
        } catch (SQLException e) {
          throw new IOException(e);
        } finally {
          closeQuery(rs, stmt);
        }
      }
    }
    return foundList;
  }

  protected abstract void setAttrs(T model, PreparedStatement stmt)
      throws SQLException;

  // ActiveRecord documentation says it supports optimistic locking using integer fields
  // That corresponds to the Integer and Long fields, but we only support locking on Integer for now
  private boolean supportsOptimisticLocking(T model) {
    return model.hasField(lockFieldName)
        && model.getField(lockFieldName) != null
        && model.getField(lockFieldName).getClass().equals(Integer.class);
  }

  private int getLockVersion(T model) {
    return (Integer)model.getField(lockFieldName);
  }

  public boolean saveStrict(long updateTimeMillis, T model) throws JackException, IOException {
    Long oldUpdatedAt = handleRailsUpdatedAt(updateTimeMillis, model);
    if (model.isCreated()) {

      if (supportsOptimisticLocking(model)) {
        UpdateStatementPreparer<T> usp = new UpdateStatementPreparer.Locking<>(
            tableName, setFieldsPrepStatementSection, lockFieldName, fieldNames.size(), getLockVersion(model));

        final boolean success = updateStrict(model, usp, oldUpdatedAt);
        if (success) {
          // If successful, update the lock_version to match what we just put in the DB
          model.setField(lockFieldName, getLockVersion(model) + 1);
        }

        return success;
      } else {
        final UpdateStatementPreparer<T> usp = new UpdateStatementPreparer.Normal<>(tableName, setFieldsPrepStatementSection);
        return updateStrict(model, usp, oldUpdatedAt);
      }

    } else {
      // optimistic locking impossible on insert
      return insertStrict(model, oldUpdatedAt);
    }
  }

  private boolean insertStrict(T model, Long oldUpdatedAt) throws JackException, IOException {
    PreparedStatement insertStmt = conn.getPreparedStatement(getInsertWithIdStatement(fieldNames));
    try {
      setAttrs(model, insertStmt);
      insertStmt.setLong(fieldNames.size() + 1, model.getId());
      insertStmt.execute();
      final int updateCount = insertStmt.getUpdateCount();
      insertStmt.close();

      if (updateCount > 1) {
        throw new TooManyRowsUpdatedException();
      } else if (updateCount < 1) {
        revertRailsUpdatedAt(model, oldUpdatedAt);
        throw new StaleModelException();
      } else {
        if (useCache) {
          cachedById.put(model.getId(), model);
        }
        clearForeignKeyCache();
        model.setCreated(true);
        return true;
      }
    } catch (SQLException e) {
      revertRailsUpdatedAt(model, oldUpdatedAt);
      throw new IOException(e);
    }
  }

  private boolean updateStrict(T model, UpdateStatementPreparer<T> usp, Long oldUpdatedAt) throws TooManyRowsUpdatedException, StaleModelException, IOException {
    try {
      final PreparedStatement saveStmt = conn.getPreparedStatement(usp.getSaveStmt());
      usp.setParams(this, model, saveStmt);
      saveStmt.execute();
      final int updateCount = saveStmt.getUpdateCount();
      saveStmt.close();

      // since the statement includes id we expect at most 1 row to be updated
      if (updateCount > 1) {
        throw new TooManyRowsUpdatedException();
      } else if (updateCount < 1) { // no row updated => id or lock_version is non-matching => stale
        // don't match Rails behaviour (updated_at changed on field modification)
        // do    match Jack  behaviour (updated_at changed on successful save)
        revertRailsUpdatedAt(model, oldUpdatedAt);
        throw new StaleModelException();
      } else {

        // Locking will not prevent clobbering if the cache is used to fetch the
        // two models since they are the same reference, and hence modifying one
        // is modifying the other.
        // We still want the right result in there, though.
        if (useCache) {
          cachedById.put(model.getId(), model);
        }
        clearForeignKeyCache();

        return true;
      }

    } catch (SQLException e) {
      revertRailsUpdatedAt(model, oldUpdatedAt);
      throw new IOException(e);
    }
  }

  @Override
  public boolean save(T model) throws IOException {
    return save(System.currentTimeMillis(), model);
  }

  @Override
  public boolean save(long updateTimeMillis, T model) throws IOException {
    try {
      return saveStrict(updateTimeMillis, model);
    } catch (JackException e) {
      return false;
    }
  }

  private static String qmarks(int size) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append("?");
    }
    return sb.toString();
  }

  public String getTableName() {
    return tableName;
  }

  @Override
  public boolean delete(long id) throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement(String.format(
        "DELETE FROM %s WHERE id=%d", tableName, id));
    try {
      boolean success = stmt.executeUpdate() == 1;
      stmt.close();
      if (success) {
        cachedById.remove(id);
      }
      clearForeignKeyCache();
      return success;
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public boolean delete(T model) throws IOException {
    return delete(model.getId());
  }

  @Override
  public boolean deleteAll() throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement(String.format(
        "TRUNCATE TABLE %s", tableName));
    try {
      boolean success = stmt.executeUpdate() >= 0;
      stmt.close();
      cachedById.clear();
      clearForeignKeyCache();
      return success;
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<T> findAll() throws IOException {
    return findAll("1=1");
  }

  @Override
  public List<T> findAll(String conditions) throws IOException {
    int retryCount = 0;

    PreparedStatement stmt = null;
    ResultSet rs = null;

    while (true) {
      try {
        stmt = conn.getPreparedStatement("SELECT * FROM "
            + getTableName() + " WHERE " + conditions + ";");

        rs = stmt.executeQuery();

        List<T> results = new ArrayList<T>();
        while (rs.next()) {
          T inst = instanceFromResultSet(rs);
          inst.setCreated(true);
          if (useCache) {
            if (cachedById.containsKey(inst.getId())) {
              inst = cachedById.get(inst.getId());
            } else {
              cachedById.put(inst.getId(), inst);
            }
          }
          results.add(inst);
        }
        return results;
      } catch (SQLRecoverableException e) {
        conn.resetConnection(e);
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      } finally {
        closeQuery(rs, stmt);
      }
    }
  }

  @Override
  public List<T> findAll(String conditions, RecordSelector<T> selector)
      throws IOException {
    return findAll(conditions);
  }

  @Override
  public boolean isCaching() {
    return useCache;
  }

  @Override
  public void enableCaching() {
    useCache = true;
  }

  @Override
  public void disableCaching() {
    useCache = false;
  }

  private boolean updatedAtCanBeHandled(T model) {
    if (model.hasField("updated_at")) {
      Object field = model.getField("updated_at");
      return (field != null && field.getClass().equals(Long.class));
    }
    return false;
  }

  private long handleRailsUpdatedAt(long updateTimeMillis, T model) {
    if (updatedAtCanBeHandled(model)) {
      long oldUpdatedAt = (Long)model.getField("updated_at");
      model.setField("updated_at", updateTimeMillis);
      // return old value in case save fails and we need to reset
      return oldUpdatedAt;
    }
    return 0;
  }

  private void revertRailsUpdatedAt(T model, long oldUpdatedAt) {
    if (updatedAtCanBeHandled(model)) {
      model.setField("updated_at", oldUpdatedAt);
    }
  }

  private void logRetryAttempt(String statementString, Throwable cause) {
    LOG.warn("Query failed: " + statementString + "\n" + "Retrying...", cause);
  }

  private void closeQuery(ResultSet resultSet, PreparedStatement statement) {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
      if (statement != null) {
        statement.close();
      }
    } catch (SQLRecoverableException e) {
      conn.resetConnection(e);
    } catch (SQLException e) {
      LOG.warn("Failed to close query", e);
    }
  }

  private abstract static class UpdateStatementPreparer<T extends ModelWithId<T, ? extends GenericDatabases>> {

    private String saveStmt;

    public UpdateStatementPreparer(String saveStmt) {
      this.saveStmt = saveStmt;
    }

    public String getSaveStmt() {
      return saveStmt;
    }

    public abstract PreparedStatement setParams(AbstractDatabaseModel<T> baseModel, T modelInstance, PreparedStatement statement) throws SQLException;

    static class Normal<A extends ModelWithId<A, ? extends GenericDatabases>> extends UpdateStatementPreparer<A> {

      public Normal(String tableName, String setFieldsPrepStatementSection) {
        super(String.format("UPDATE %s SET %s WHERE id=?;", tableName, setFieldsPrepStatementSection));
      }

      @Override
      public PreparedStatement setParams(AbstractDatabaseModel<A> baseModel, A modelInstance, PreparedStatement statement) throws SQLException {
        baseModel.setAttrs(modelInstance, statement);
        return statement;
      }
    }

    static class Locking<A extends ModelWithId<A, ? extends GenericDatabases>> extends UpdateStatementPreparer<A> {

      private final int numFieldNames;
      private final int lockVersion;

      Locking(String tableName, String setFieldsPrepStatementSection, String lockFieldName, int numFieldNames, int lockVersion) {
        super(String.format("UPDATE %s SET %s WHERE id=? AND %s=?;", tableName, setFieldsPrepStatementSection, lockFieldName));
        this.numFieldNames = numFieldNames;
        this.lockVersion = lockVersion;
      }

      @Override
      public PreparedStatement setParams(AbstractDatabaseModel<A> baseModel, A modelInstance, PreparedStatement statement) throws SQLException {
        baseModel.setAttrs(modelInstance, statement);
        // the lock_version is after all the field params and the id param
        statement.setLong(numFieldNames + 2, lockVersion);
        return statement;
      }
    }
  }
}
