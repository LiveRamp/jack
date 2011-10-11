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
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDatabaseModel<T extends ModelWithId> implements IModelPersistence<T> {
  protected static interface AttrSetter {
    public void set(PreparedStatement stmt) throws SQLException;
  }

  private final DatabaseConnection conn;
  private final String tableName;

  private final List<String> fieldNames;
  private final String updateStatement;

  protected final Map<Integer, T> cachedById = new HashMap<Integer, T>();
  protected final Map<String, Map<Integer, Set<T>>> cachedByForeignKey = new HashMap<String, Map<Integer, Set<T>>>();

  protected AbstractDatabaseModel(DatabaseConnection conn, String tableName, List<String> fieldNames) {
    this.conn = conn;
    this.tableName = tableName;
    this.fieldNames = fieldNames;
    updateStatement = String.format("UPDATE %s SET %s WHERE id = ?;", tableName, getSetFieldsPrepStatementSection());
  }

  protected String getInsertStatement(List<String> fieldNames) {
    return String.format("INSERT INTO %s (%s) VALUES(%s);",
        tableName, escapedFieldNames(fieldNames), qmarks(fieldNames.size()));
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

  protected DatabaseConnection getConn() {
    return conn;
  }

  protected abstract T instanceFromResultSet(ResultSet rs) throws SQLException;

  protected int realCreate(AttrSetter attrSetter, String insertStatement) throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement(insertStatement);
    try {
      attrSetter.set(stmt);
      stmt.execute();
      ResultSet generatedKeys = stmt.getGeneratedKeys();
      generatedKeys.next();
      int newId = generatedKeys.getInt(1);
      return newId;
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

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
    if (model != null) {
      return model;
    }

    try {
      ResultSet rs = conn.getPreparedStatement("SELECT * FROM " + tableName + " WHERE id=" + id).executeQuery();
      model = rs.next() ? instanceFromResultSet(rs) : null;
    } catch (SQLException e) {
      throw new IOException(e);
    }

    cachedById.put(id, model);
    return model;
  }

  protected PreparedStatement getSaveStmt() {
    return conn.getPreparedStatement(updateStatement);
  }

  protected final static Integer getIntOrNull(ResultSet rs, String column) throws SQLException {
    Integer value = rs.getInt(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Long getLongOrNull(ResultSet rs, String column) throws SQLException {
    Long value = rs.getLong(column);
    return rs.wasNull() ? null : value;
  }

  protected final static Date getDate(ResultSet rs, String column) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(column);
    if (timestamp == null) {
      return null;
    }
    return new Date(timestamp.getTime());
  }

  protected final static Long getDateAsLong(ResultSet rs, String column ) throws SQLException {
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
  public Set<T> findAllByForeignKey(String foreignKey, int id) throws IOException {
    Map<Integer, Set<T>> foreignKeyCache = cachedByForeignKey.get(foreignKey);
    Set<T> ret;
    if (foreignKeyCache != null) {
      ret = foreignKeyCache.get(id);
      if (ret != null) {
        return ret;
      }
    } else {
      foreignKeyCache = new HashMap<Integer, Set<T>>();
      cachedByForeignKey.put(foreignKey, foreignKeyCache);
    }
    
    PreparedStatement stmt = conn.getPreparedStatement(String.format("SELECT * FROM %s WHERE %s = %d;", tableName, foreignKey, id));
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();
      ret = new HashSet<T>();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs);
        T cachedInst = cachedById.get(inst.getId());
        if (cachedInst == null) {
          cachedById.put(inst.getId(), inst);
          ret.add(inst);
        } else {
          ret.add(cachedInst);
        }
      }
      
      foreignKeyCache.put(id, ret);
      
      return ret;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  protected abstract void setAttrs(T model, PreparedStatement stmt) throws SQLException;

  @Override
  public boolean save(T model) throws IOException {
    PreparedStatement saveStmt = getSaveStmt();
    try {
      setAttrs(model, saveStmt);
      saveStmt.execute();
      return saveStmt.getUpdateCount() == 1;
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
    try {
      cachedById.remove(id);
      return conn.getPreparedStatement(String.format("DELETE FROM %s WHERE id=%d", tableName, id)).executeUpdate() == 1;
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
    try {
      return conn.getPreparedStatement(String.format("TRUNCATE TABLE %s", tableName)).executeUpdate() >= 0;
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public Set<T> findAll() throws IOException {
    return findAll("true");
  }

  @Override
  public Set<T> findAll(String conditions) throws IOException {
    PreparedStatement stmt = conn.getPreparedStatement("SELECT * FROM " + getTableName() + " WHERE " + conditions + ";");
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery();

      Set<T> results = new HashSet<T>();
      while (rs.next()) {
        T inst = instanceFromResultSet(rs);
        T cachedInst = cachedById.get(inst.getId());
        if (cachedInst == null) {
          cachedById.put(inst.getId(), inst);
          results.add(inst);
        } else {
          results.add(cachedInst);
        }
      }
      return results;
    } catch (SQLException e) {
      throw new IOException(e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          throw new IOException(e);
        }
      }
    }
  }
}
