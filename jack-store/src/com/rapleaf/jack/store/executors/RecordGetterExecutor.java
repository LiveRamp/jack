package com.rapleaf.jack.store.executors;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;
import com.rapleaf.jack.transaction.ITransactor;

public class RecordGetterExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Set<String> selectedKeys;

  protected RecordGetterExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.selectedKeys = Sets.newHashSet();
  }

  public RecordGetterExecutor<DB> select(String key, String... otherKeys) {
    selectedKeys.add(key);
    selectedKeys.addAll(Arrays.asList(otherKeys));
    return this;
  }

  public RecordGetterExecutor<DB> select(Collection<String> keys) {
    selectedKeys.addAll(keys);
    return this;
  }

  @SuppressWarnings("unchecked")
  public JsRecord get() {
    Optional<JsScope> recordScope = getOptionalExecutionScope();
    if (!recordScope.isPresent()) {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }

    Records records = transactor.queryAsTransaction(db ->
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(recordScope.get().getScopeId()))
            .where(table.typeColumn.notEqualTo(JsConstants.SCOPE_TYPE))
            .select(table.typeColumn, table.keyColumn, table.valueColumn)
            .orderBy(table.idColumn)
            .fetch()
    );

    Map<String, ValueType> types = Maps.newHashMap();
    Map<String, Object> values = Maps.newHashMap();
    List<JsonDbTuple> jsonTuples = Lists.newLinkedList();
    Set<String> jsonKeys = Sets.newHashSet();

    for (Record record : records) {
      ValueType type = ValueType.valueOf(record.get(table.typeColumn));
      String key = record.get(table.keyColumn);
      String value = record.get(table.valueColumn);

      switch (type.getCategory()) {
        case PRIMITIVE:
          if (!isSelectedKey(key)) {
            continue;
          }
          types.put(key, type);
          values.put(key, value);
          break;
        case JSON:
          JsonDbTuple tuple = JsonDbTuple.create(key, type, value);
          String jsonKey = tuple.getPaths().get(0).getName().get();
          if (!isSelectedKey(jsonKey)) {
            continue;
          }
          jsonKeys.add(jsonKey);
          jsonTuples.add(tuple);
          break;
        case LIST:
          if (!isSelectedKey(key)) {
            continue;
          }
          types.put(key, type);
          if (!values.containsKey(key)) {
            values.put(key, Lists.newArrayList());
          }
          if (value != null) {
            ((List<Object>)values.get(key)).add(value);
          }
          break;
        default:
          throw new IllegalStateException("Unexpected type: " + type.name());
      }
    }

    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonTuples);
    for (String jsonKey : jsonKeys) {
      JsonObject json = jsonObject.get(jsonKey).getAsJsonObject();
      types.put(jsonKey, ValueType.JSON_STRING);
      values.put(jsonKey, json);
    }

    if (types.isEmpty()) {
      return JsRecord.empty();
    } else {
      return new JsRecord(types, values);
    }
  }

  private boolean isSelectedKey(String key) {
    return selectedKeys.isEmpty() || selectedKeys.contains(key);
  }

}
