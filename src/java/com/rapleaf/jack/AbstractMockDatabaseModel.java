package com.rapleaf.jack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.util.MysqlToJavaScriptTranslator;

public abstract class AbstractMockDatabaseModel<T extends ModelWithId<T, D>, D extends GenericDatabases>
    implements IModelPersistence<T> {

  private final D databases;
  protected final Map<Long, T> records = new HashMap<Long, T>();

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
            query.append(((String)fieldValue).replaceAll("\"", "\\\\\""));
            query.append('"');
          } else {
            query.append(fieldValue);
          }
          query.append(";");
        }
        query.append(conditionJS);
        Object result = engine.eval(query.toString(), engine.createBindings());
        if (result instanceof Boolean) {
          return (Boolean)result;
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

  public AbstractMockDatabaseModel(D databases) {
    this.databases = databases;
  }

  protected Set<T> realFind(Map<Enum, Object> fieldsMap) throws IOException {
    return realFind(null, fieldsMap);
  }

  protected Set<T> realFind(Set<Long> ids, Map<Enum, Object> fieldsMap) throws IOException {
    Set<T> foundSet = new HashSet<T>();
    if (fieldsMap == null || fieldsMap.isEmpty()) {
      return foundSet;
    }

    for (T record : records.values()) {
      boolean allMatch = true;
      for (Map.Entry<Enum, Object> e : fieldsMap.entrySet()) {
        Object searchedForValue = e.getValue();
        Object existingValue = record.getField(e.getKey().name());
        if (ids != null && !ids.contains(record.getId())) {
          allMatch = false;
        }
        if (existingValue == null) {
          if (searchedForValue != null) {
            allMatch = false;
          }
        } else if (!existingValue.equals(searchedForValue)) {
          allMatch = false;
        }
      }
      if (allMatch) {
        foundSet.add(record);
      }
    }

    return foundSet;
  }

  protected Set<T> realFind(ModelQuery query) throws IOException {
    Set<T> foundSet = new HashSet<T>();

    List<QueryConstraint> constraints = query.getConstraints();
    Set<Long> ids = query.getIdSet();
    if (constraints == null || constraints.isEmpty()) {
      if (ids != null && !ids.isEmpty()) {
        return find(ids);
      }
      return foundSet;
    }
    for (T record : records.values()) {
      boolean allMatch = true;
      for (QueryConstraint constraint : constraints) {

        Enum field = constraint.getField();
        IQueryOperator operator = constraint.getOperator();
        allMatch = allMatch && operator.apply(record.getField(field.name()));
      }
      if (ids != null && !ids.isEmpty() && !ids.contains(record.getId())) {
        allMatch = false;
      }
      if (allMatch) {
        foundSet.add(record);
      }
    }

    return foundSet;
  }
  
  protected List<T> realFindWithOrder(ModelQuery query) throws IOException {
    // currently ordered query is not supported for mock database model
    return null;
  }

  @Override
  public boolean save(T model) throws IOException {
    records.put(model.getId(), model);
    clearForeignKeyCache();
    return true;
  }

  @Override
  public T find(long id) throws IOException {
    final T tmp = records.get(id);
    if (tmp == null) {
      return null;
    }
    return tmp.getCopy(databases);
  }

  @Override
  public Set<T> find(Set<Long> ids) throws IOException {
    Set<T> results = new HashSet<T>();
    for (Long id : ids) {
      T result = records.get(id);
      if (results != null) {
        results.add(result);
      }
    }
    return results;
  }

  public List<T> findWithOrder(Set<Long> ids, ModelQuery query) throws IOException {
    // currently ordered query is not supported for mock database model
    return null;
  }
  
  @Override
  public void clearCacheById(long id) throws IOException {
    // No-op
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, long id)
      throws IOException {
    Set<T> ret = new HashSet<T>();
    for (T record : records.values()) {
      Object foreignKeyValue = record.getField(foreignKey);
      if (foreignKeyValue instanceof Long) {
        if (foreignKeyValue.equals(id)) {
          ret.add(record.getCopy(databases));
        }
      } else if (foreignKeyValue instanceof Integer) {
        if (((Integer)foreignKeyValue).longValue() == id) {
          ret.add(record.getCopy(databases));
        }
      } else {
        throw new IllegalArgumentException("Foreign key is not a long or int: "
            + foreignKey);
      }
    }
    return ret;
  }

  @Override
  public Set<T> findAllByForeignKey(String foreignKey, Set<Long> ids)
      throws IOException {
    Set<T> foundSet = new HashSet<T>();
    for (T record : records.values()) {
      Object foreignKeyValue = record.getField(foreignKey);
      if (foreignKeyValue instanceof Long) {
        if (ids.contains(foreignKeyValue)) {
          foundSet.add(record.getCopy(databases));
        }
      } else if (foreignKeyValue instanceof Integer) {
        if (ids.contains(((Integer)foreignKeyValue).longValue())) {
          foundSet.add(record.getCopy(databases));
        }
      } else {
        throw new IllegalArgumentException("Foreign key is not a long or int: "
            + foreignKey);
      }
    }
    return foundSet;
  }

  @Override
  public void clearCacheByForeignKey(String foreignKey, long id) {
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
  public boolean delete(long id) throws IOException {
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
    Set<T> ts = new HashSet<T>();
    for (T t : records.values()) {
      ts.add(t.getCopy(databases));
    }
    return ts;
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
        results.add(record.getCopy(databases));
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
