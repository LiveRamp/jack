package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.DateTime;
import org.junit.Before;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JackStore;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.transaction.IExecution;
import com.rapleaf.jack.transaction.ITransactor;

public class BaseExecutorTestCase {

  protected static final String BOOLEAN_KEY = "boolean";
  protected static final boolean BOOLEAN_VALUE = false;

  protected static final String INT_KEY = "int";
  protected static final int INT_VALUE = 10;

  protected static final long LONG_VALUE = 20L;
  protected static final String LONG_KEY = "long";

  protected static final String DOUBLE_KEY = "double";
  protected static final double DOUBLE_VALUE = 30.5;

  protected static final String DATETIME_KEY = "datetime";
  protected static final DateTime DATETIME_VALUE = DateTime.now();

  protected static final String STRING_KEY = "string";
  protected static final String STRING_VALUE = "s40";

  protected static final String JSON_KEY = "json";
  protected static final String JSON_STRING = "{key: [[1, 2, [[3]]], [{k1: v1, k2: v2}, 4, 5], {k3: v3}]}";
  protected static final JsonObject JSON_VALUE = new JsonParser().parse(JSON_STRING).getAsJsonObject();

  protected static final String BOOLEAN_LIST_KEY = "boolean-list";
  protected static final List<Boolean> BOOLEAN_LIST_VALUE = Lists.newArrayList(true, true, false);

  protected static final String INT_LIST_KEY = "int-list";
  protected static final List<Integer> INT_LIST_VALUE = Lists.newArrayList(50, 60, 70);

  protected static final String LONG_LIST_KEY = "long-list";
  protected static final List<Long> LONG_LIST_VALUE = Lists.newArrayList(80L, 90L);

  protected static final String DOUBLE_LIST_KEY = "double-list";
  protected static final List<Double> DOUBLE_LIST_VALUE = Lists.newArrayList(100.5, 110.5);

  protected static final String DATETIME_LIST_KEY = "datetime-list";
  protected static final List<DateTime> DATETIME_LIST_VALUE = Lists.newArrayList(DateTime.now(), DateTime.now().minusDays(1));

  protected static final String STRING_LIST_KEY = "string-list";
  protected static final List<String> STRING_LIST_VALUE = Lists.newArrayList("s120", "s130", "s140");

  protected final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  protected final JackStore<IDatabase1> jackStore = new JackStore<>(transactor, TestStore.TBL, TestStore.SCOPE, TestStore.TYPE, TestStore.KEY, TestStore.VALUE);

  protected Records records;

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction((IExecution<IDatabase1>)IDb::deleteAll);
  }

  protected JsScope createScope() {
    return jackStore.withinRoot().createScope().execute();
  }

  protected JsScope createScope(List<String> parentScopes) {
    return jackStore.within(parentScopes).createScope().execute();
  }

  protected JsScope createScope(String newScope) {
    return jackStore.withinRoot().createScope(newScope).execute();
  }

  protected JsScope createScope(List<String> parentScopes, String newScope) {
    return jackStore.within(parentScopes).createScope(newScope).execute();
  }

  protected JsScope createScope(JsScope parentScope, String newScope) {
    return jackStore.within(parentScope).createScope(newScope).execute();
  }

  protected List<String> list(String element) {
    return Collections.singletonList(element);
  }

}
