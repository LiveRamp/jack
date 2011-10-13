package com.rapleaf.jack;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rapleaf.jack.util.MysqlToJavaScriptTranslator;

public abstract class AbstractMockDatabaseModel<T extends ModelWithId>
    implements IModelPersistence<T> {

  protected final Map<Integer, T> records = new HashMap<Integer, T>();
  
  private static class JavaScriptRecordSelector<T extends ModelWithId>
      implements RecordSelector<T> {

    private final String condition;
    private static final ScriptEngine engine = new ScriptEngineManager()
        .getEngineByName("javascript");

    public JavaScriptRecordSelector(String condition) {
      this.condition = condition;
    }

    @Override
    public boolean selectRecord(T record) {
      try {
        Set<String> referencedFields = new HashSet<String>();
        String conditionJS = MysqlToJavaScriptTranslator.translate(condition,
            referencedFields);
        StringBuilder query = new StringBuilder();
        for (String field : referencedFields) {
          Object fieldValue = record.getField(field);
          query.append("var ");
          query.append(field);
          query.append("=");
          if (fieldValue instanceof String) {
            query.append('"');
            query.append(((String) fieldValue).replaceAll("\"", "\\\\\""));
            query.append('"');
          } else {
            query.append(fieldValue);
          }
          query.append(";");
        }
        query.append(conditionJS);
        Object result = engine.eval(query.toString(), engine.createBindings());
        if (result instanceof Boolean) {
          return (Boolean) result;
        } else {
          throw new RuntimeException(
              "Failed to translate condition from MySQL to JavaScript.  Unsupported or erroneous SQL syntax is likely. Where clause: " + condition);
        }
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to translate condition from MySQL to JavaScript.  Unsupported or erroneous SQL syntax is likely. Where clause: " + condition,
            e);
      } catch (ScriptException e) {
        throw new RuntimeException(
            "Failed to translate condition from MySQL to JavaScript.  Unsupported or erroneous SQL syntax is likely. Where clause: " + condition,
            e);
      }
    }

  }

  protected Set<T> realFind(Map fieldsMap) throws IOException {
    Set<T> foundSet = new HashSet<T>();
    if (fieldsMap == null || fieldsMap.isEmpty()) {
      return foundSet;
    }

    for (T record : records.values()) {
      boolean allMatch = true;
      for (Map.Entry<Enum, Object> e : ((Map<Enum, Object>)fieldsMap).entrySet()) {
        Object searchedForValue = e.getValue();
        Object existingValue = record.getField(e.getKey().name());
        if (existingValue == null){
          if (searchedForValue != null) allMatch = false;
        } else if (!existingValue.equals(searchedForValue)){
          allMatch = false;
        }
        if (allMatch) foundSet.add(record);
      }
    }

    return foundSet;
  }

  @Override
  public boolean save(T model) throws IOException {
    records.put(model.getId(), model);
    clearForeignKeyCache();
    return true;
  }

  @Override
  public T find(int id) throws IOException {
    return records.get(id);
  }

  @Override
  public Set<T> find(Set<Integer> ids) throws IOException {
    Set<T> results = new HashSet<T>();
    for (Integer id : ids) {
      T result = records.get(id);
      if (results != null) {
        results.add(result);
      }
    }
    return results;
  }

  @Override
  public void clearCacheById(int id) throws IOException {
    // No-op
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, int id)
      throws IOException {
    Set<T> ret = new HashSet<T>();
    for (T record : records.values()) {
      Object foreignKeyValue = record.getField(foreignKey);
      if (foreignKeyValue instanceof Integer) {
        if (foreignKeyValue.equals(id)) {
          ret.add(record);
        }
      } else {
        throw new IllegalArgumentException("Foreign key is not an integer: "
            + foreignKey);
      }
    }
    return ret;
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, Set<Integer> ids)
      throws IOException {
    Set<T> foundSet = new HashSet<T>();
    for (T record : records.values()) {
      Object foreignKeyValue = record.getField(foreignKey);
      if (foreignKeyValue instanceof Integer) {
        if (ids.contains(foreignKeyValue)) {
          foundSet.add(record);
        }
      } else {
        throw new IllegalArgumentException("Foreign key is not an integer: "
            + foreignKey);
      }
    }
    return foundSet;
  }

  @Override
  public void clearCacheByForeignKey(String foreignKey, int id) {
    // no-op
  }

  @Override
  public void clearForeignKeyCache() {
    // no-op
  }

  @Override
  public boolean delete(T model) throws IOException {
    return delete(model.getId());
  }

  @Override
  public boolean delete(int id) throws IOException {
    records.remove(id);
    return true;
  }

  @Override
  public boolean deleteAll() throws IOException {
    records.clear();
    return true;
  }

  @Override
  public Set<T> findAll() throws IOException {
    return Collections.unmodifiableSet(new HashSet<T>(records.values()));
  }

  @Override
  public Set<T> findAll(String conditions) throws IOException {
    return findAll(conditions, getRecordSelector(conditions));
  }

  @Override
  public Set<T> findAll(String conditions, RecordSelector<T> selector) {
    Set<T> results = new HashSet<T>();
    for (T record : records.values()) {
      if (selector.selectRecord(record)) {
        results.add(record);
      }
    }
    return results;
  }

  protected RecordSelector<T> getRecordSelector(String conditions)
      throws IOException {
    return new JavaScriptRecordSelector(conditions);
  }
  
  private boolean useCache = true;
  
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
