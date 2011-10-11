//
// Copyright 2011 Rapleaf
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDatabaseModel<T extends ModelWithId> implements
    IModelPersistence<T> {
  protected static interface AttrSetter {
    public void set(PreparedStatement stmt) throws SQLException;
  }

  private final BaseDatabaseConnection conn;
  private final String tableName;

  private final List<String> fieldNames;
  private final String updateStatement;

  protected final Map<Integer, T> cachedById = new HashMap<Integer, T>();
  protected final Map<String, Map<Integer, Set<T>>> cachedByForeignKey = new HashMap<String, Map<Integer, Set<T>>>();
  
  private boolean useCache = true;

  protected AbstractDatabaseModel(BaseDatabaseConnection conn,
      String tableName, List<String> fieldNames) {
    this.conn = conn;
    this.tableName = tableName;
    this.fieldNames = fieldNames;
    updateStatement = String.format(
        "INSERT INTO %s SET %s , id=? ON DUPLICATE KEY UPDATE %s;", tableName,
        getSetFieldsPrepStatementSection(), getUpdateOnInsertPrepStatementSection());
  }

  protected String getInsertStatement(List<String> fieldNames) {
    return String.format("INSERT INTO %s (%s) VALUES(%s);", tableName,
        escapedFieldNames(fieldNames), qmarks(fieldNames.size()));
  }

  private String getSetFieldsPrepStatementSection() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append("`").append(fieldNames.get(i)).append("` = ?");
    }
    return sb.toString();
  }

  private String getUpdateOnInsertPrepStatementSection() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append("`").append(fieldNames.get(i)).append("` = VALUES(`").append(fieldNames.get(i)).append("`)");
    }
    return sb.toString();
  }

  protected BaseDatabaseConnection getConn() {
    return conn;
  }

  protected abstract T instanceFromResultSet(ResultSet rs) throws SQLException;

  protected int realCreate(AttrSetter attrSetter, String insertStatement)
      throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement(insertStatement);
    ResultSet generatedKeys = null;
    try {
      attrSetter.set(stmt);
      stmt.execute();
      generatedKeys = stmt.getGeneratedKeys();
      generatedKeys.next();
      int newId = generatedKeys.getInt(1);
      return newId;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (generatedKeys != null) {
          generatedKeys.close();
        }
        stmt.close();
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  public abstract ModelWithId create(Map<Enum, Object> fieldsMap) throws IOException;

  private String escapedFieldNames(List<String> fieldNames) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i != 0) {
        sb.append(", ");
      }
      sb.append("`").append(fieldNames.get(i)).append("`");
    }
    return sb.toString();
  }

  public T find(int id) throws IOException {
    T model = cachedById.get(id);
    if (model != null && useCache) {
      return model;
    }
    PreparedStatement stmt = conn.getPreparedStatement("SELECT * FROM "
        + tableName + " WHERE id=" + id);
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();
      model = rs.next() ? instanceFromResultSet(rs) : null;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        stmt.close();
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
    if (useCache) {
      cachedById.put(id, model);
    }
    return model;
  }

  public Set<T> find(Set<Integer> ids) throws IOException {
    Set<T> foundSet = new HashSet<T>();
    Set<Integer> notCachedIds = new HashSet<Integer>();
    if (useCache) {
      for (Integer id : ids) {
        T model = cachedById.get(id);
        if (model != null) {
          foundSet.add(model);
        } else {
          notCachedIds.add(id);
        }
      }
    } else {
      notCachedIds = ids;
    }
    if (!notCachedIds.isEmpty()) {
      StringBuilder statementString = new StringBuilder();
      statementString.append("SELECT * FROM ");
      statementString.append(tableName);
      statementString.append(" WHERE id in (");
      Iterator<Integer> iter = notCachedIds.iterator();
      while (iter.hasNext()) {
        Integer obj = iter.next();
        statementString.append(obj.toString());
        if (iter.hasNext()) {
          statementString.append(",");
        }
      }
      statementString.append(")");
      executeQuery(foundSet, statementString);
    }
    return foundSet;
  }

  protected Set<T> realFind(Map fieldsMap) throws IOException {
    Set<T> foundSet = new HashSet<T>();
    if (fieldsMap == null || fieldsMap.isEmpty()) {
      return foundSet;
    }

    StringBuilder statementString = new StringBuilder();
    statementString.append("SELECT * FROM ");
    statementString.append(tableName);
    statementString.append(" WHERE (");


    Iterator<Map.Entry<Enum, Object>> iter = fieldsMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Enum, Object> entry = iter.next();
      statementString.append(entry.getKey() + " = \"" + entry.getValue().toString() + "\"");
      if (iter.hasNext()) {
        statementString.append(" AND ");
      }
    }
    statementString.append(")");
    executeQuery(foundSet, statementString);

    return foundSet;
  }

  private void executeQuery(Set<T> foundSet, StringBuilder statementString) throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement(statementString
        .toString());
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs);
        foundSet.add(inst);
        if (useCache) {
          cachedById.put(inst.getId(), inst);
        }
      }
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        stmt.close();
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  protected PreparedStatement getSaveStmt() {
    return conn.getPreparedStatement(updateStatement);
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
  public void clearCacheByForeignKey(String foreignKey, int id) {
    Map<Integer, Set<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    if (foreignKeyCache != null) {
      foreignKeyCache.remove(id);
    }
  }

  @Override
  public void clearCacheById(int id) throws IOException {
    cachedById.remove(id);
  }

  @Override
  public void clearForeignKeyCache() {
    cachedByForeignKey.clear();
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, int id)
      throws IOException {
    Map<Integer, Set<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    Set<T> ret;
    if (foreignKeyCache != null && useCache) {
      ret = foreignKeyCache.get(id);
      if (ret != null) {
        return ret;
      }
    } else if (useCache) {
      foreignKeyCache = new HashMap<Integer, Set<T>>();
      cachedByForeignKey.put(foreignKey, foreignKeyCache);
    }

    PreparedStatement stmt = conn.getPreparedStatement(String.format(
        "SELECT * FROM %s WHERE %s = %d;", tableName, foreignKey, id));
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();
      ret = new HashSet<T>();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs);
        if (useCache) {
          T cachedInst = cachedById.get(inst.getId());
          if (cachedInst == null) {
            cachedById.put(inst.getId(), inst);
          } else {
            inst = cachedInst;
          }
        }
        ret.add(inst);
      }
      if (useCache) {
        foreignKeyCache.put(id, ret);
      }
      return ret;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        stmt.close();
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, Set<Integer> ids)
      throws IOException {
    Map<Integer, Set<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    Set<T> foundSet = new HashSet<T>();
    Set<Integer> notCachedIds = new HashSet<Integer>();
    if (foreignKeyCache != null && useCache) {
      for (Integer id : ids) {
        Set<T> results = foreignKeyCache.get(id);
        if (results != null) {
          foundSet.addAll(results);
        } else {
          notCachedIds.add(id);
        }
      }
    } else {
      notCachedIds = ids;
      if (useCache) {
        foreignKeyCache = new HashMap<Integer, Set<T>>();
        cachedByForeignKey.put(foreignKey, foreignKeyCache);
      }
    }

    if (!notCachedIds.isEmpty()) {
      StringBuilder statementString = new StringBuilder();
      statementString.append("SELECT * FROM ");
      statementString.append(tableName);
      statementString.append(" WHERE " + foreignKey + " in (");
      Iterator<Integer> iter = notCachedIds.iterator();
      while (iter.hasNext()) {
        Integer obj = iter.next();
        statementString.append(obj.toString());
        if (iter.hasNext()) {
          statementString.append(",");
        }
      }
      statementString.append(")");
      PreparedStatement stmt = conn.getPreparedStatement(statementString
          .toString());
      ResultSet rs = null;
      try {
        rs = stmt.executeQuery();
        while (rs.next()) {
          T inst = instanceFromResultSet(rs);
          foundSet.add(inst);
          if (useCache) {
            cachedById.put(inst.getId(), inst);
          }
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
    return foundSet;
  }

  protected abstract void setAttrs(T model, PreparedStatement stmt)
      throws SQLException;

  @Override
  public boolean save(T model) throws IOException {
    PreparedStatement saveStmt = getSaveStmt();
    try {
      setAttrs(model, saveStmt);
      saveStmt.execute();
      boolean success = saveStmt.getUpdateCount() == 1;
      saveStmt.close();
      if (success && useCache) {
        cachedById.put(model.getId(), model);
      }
      clearForeignKeyCache();
      return success;
    } catch (SQLException e) {
      throw new IOException(e);
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
  public boolean delete(int id) throws IOException {
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
  public Set<T> findAll() throws IOException {
    return findAll("1=1");
  }

  @Override
  public Set<T> findAll(String conditions) throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement("SELECT * FROM "
        + getTableName() + " WHERE " + conditions + ";");
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();

      Set<T> results = new HashSet<T>();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs);
        if (useCache) {
          T cachedInst = cachedById.get(inst.getId());
          if (cachedInst == null) {
            cachedById.put(inst.getId(), inst);
          } else {
            inst = cachedInst;
          }
        }
        results.add(inst);
      }
      return results;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        stmt.close();
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  @Override
  public Set<T> findAll(String conditions, RecordSelector<T> selector)
      throws IOException {
    return findAll(conditions);
  }
  
  public boolean isCaching() {
    return useCache;
  }
  
  public void enableCaching() {
    useCache = true;
  }
  
  public void disableCaching() {
    useCache = false;
  }
}
