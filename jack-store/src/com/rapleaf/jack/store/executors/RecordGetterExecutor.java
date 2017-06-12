package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.transaction.ITransactor;

public class RecordGetterExecutor<DB extends IDb> extends BaseExecutor<DB> {

  protected RecordGetterExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
  }

  public JsRecord get() {
    Optional<JsScope> recordScope = getOptionalExecutionScope();
    if (!recordScope.isPresent()) {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }

    Records records = transactor.queryAsTransaction(db ->
        db.createQuery().from(table.table)
            .where(table.scopeColumn.as(Long.class).equalTo(recordScope.get().getScopeId()))
            .where(table.typeColumn.notEqualTo(JsConstants.SCOPE_TYPE))
            .select(table.typeColumn, table.keyColumn, table.valueColumn)
            .fetch()
    );

    Map<String, JsConstants.ValueType> types = Maps.newHashMap();
    Map<String, Object> values = Maps.newHashMap();

    for (Record record : records) {
      JsConstants.ValueType type = JsConstants.ValueType.valueOf(record.get(table.typeColumn));
      String key = record.get(table.keyColumn);
      String value = record.get(table.valueColumn);

      types.put(key, type);
      if (type.isList()) {
        if (!values.containsKey(key)) {
          values.put(key, Lists.newArrayList());
        }
        if (value != null) {
          ((List<Object>)values.get(key)).add(value);
        }
      } else {
        values.put(key, value);
      }
    }
    return new JsRecord(types, values);
  }

}
