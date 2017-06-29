package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.DateTime;
import org.junit.Before;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JackStore;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.json.BaseJsonTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.transaction.IExecution;
import com.rapleaf.jack.transaction.ITransactor;

public class BaseExecutorTestCase extends JackTestCase {

  static final JsonParser JSON_PARSER = new JsonParser();

  static final String BOOLEAN_KEY = "boolean";
  static final boolean BOOLEAN_VALUE = false;

  static final String INT_KEY = "int";
  static final int INT_VALUE = 10;

  static final long LONG_VALUE = 20L;
  static final String LONG_KEY = "long";

  static final String DOUBLE_KEY = "double";
  static final double DOUBLE_VALUE = 30.5;

  static final String DATETIME_KEY = "datetime";
  static final DateTime DATETIME_VALUE = DateTime.now();

  static final String STRING_KEY = "string";
  static final String STRING_VALUE = "s40";

  static final String JSON_KEY = "json";
  static final String JSON_STRING = BaseJsonTestCase.COMPLEX_JSON_STRING;
  static final JsonObject JSON_VALUE = JSON_PARSER.parse(JSON_STRING).getAsJsonObject();

  static final String BOOLEAN_LIST_KEY = "boolean-list";
  static final List<Boolean> BOOLEAN_LIST_VALUE = Lists.newArrayList(true, true, false);

  static final String INT_LIST_KEY = "int-list";
  static final List<Integer> INT_LIST_VALUE = Lists.newArrayList(50, 60, 70);

  static final String LONG_LIST_KEY = "long-list";
  static final List<Long> LONG_LIST_VALUE = Lists.newArrayList(80L, 90L);

  static final String DOUBLE_LIST_KEY = "double-list";
  static final List<Double> DOUBLE_LIST_VALUE = Lists.newArrayList(100.5, 110.5);

  static final String DATETIME_LIST_KEY = "datetime-list";
  static final List<DateTime> DATETIME_LIST_VALUE = Lists.newArrayList(DateTime.now(), DateTime.now().minusDays(1));

  static final String STRING_LIST_KEY = "string-list";
  static final List<String> STRING_LIST_VALUE = Lists.newArrayList("s120", "s130", "s140");

  final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  final JackStore<IDatabase1> jackStore = new JackStore<>(transactor, JsTable.from(TestStore.TBL).create());

  Records records;

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction((IExecution<IDatabase1>)IDb::deleteAll);
  }

  JsScope createScope() {
    return jackStore.rootScope().createScope().execute();
  }

  JsScope createScope(List<String> parentScopes) {
    return jackStore.scope(parentScopes).createScope().execute();
  }

  JsScope createScope(String newScope) {
    return jackStore.rootScope().createScope(newScope).execute();
  }

  JsScope createScope(List<String> parentScopes, String newScope) {
    return jackStore.scope(parentScopes).createScope(newScope).execute();
  }

  JsScope createScope(JsScope parentScope, String newScope) {
    return jackStore.scope(parentScope).createScope(newScope).execute();
  }

  protected List<String> list(String element) {
    return Collections.singletonList(element);
  }

}
