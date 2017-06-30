package com.rapleaf.jack.store.executors;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonParser;
import org.junit.Test;

import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubScopeQueryExecutor extends BaseExecutorTestCase {

  @Test
  public void testParentScopeId() throws Exception {
    JsScope parentScope = transactor.query(db -> jackStore.rootScope().createSubScope().execute(db));
    JsScopes childScopes = transactor.query(db -> {
      for (int i = 0; i < 5; ++i) {
        jackStore.scope(parentScope).createSubScope().execute(db);
      }
      return jackStore.scope(parentScope).querySubScopes().execute(db);
    });
    Set<Long> parentIds = childScopes.getScopes().stream().map(JsScope::getParentScopeId).collect(Collectors.toSet());
    assertEquals(1, parentIds.size());
    assertEquals(parentScope.getScopeId(), parentIds.iterator().next());
  }

  @Test
  public void testNoConstraint() throws Exception {
    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().execute(db));
    assertEquals(0, scopes.size());

    // three scopes under root
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().execute(db));
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(scopes.getScopes()));

    // three scopes under scope 1
    JsScope s01 = createScope(list("1"), "1");
    JsScope s02 = createScope(list("1"), "2");
    JsScope s03 = createScope(list("1"), "3");

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().execute(db));
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(scopes.getScopes()));

    scopes = transactor.query(db -> jackStore.scope("1").querySubScopes().execute(db));
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s01, s02, s03), Sets.newHashSet(scopes.getScopes()));
  }

  @Test
  public void testScopeConstraint() throws Exception {
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    // name constraint
    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeName(JackMatchers.greaterThan("2"))
        .execute(db)
    );
    assertEquals(1, scopes.size());
    assertEquals(s3, scopes.getScopes().get(0));

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeName(JackMatchers.greaterThanOrEqualTo("2"))
        .execute(db)
    );
    assertEquals(2, scopes.size());
    assertEquals(Sets.newHashSet(s2, s3), Sets.newHashSet(scopes.getScopes()));

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeName(JackMatchers.isNull())
        .execute(db)
    );
    assertTrue(scopes.isEmpty());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeName(JackMatchers.equalTo("2"))
        .execute(db)
    );
    assertEquals(s2, scopes.getScopes().get(0));

    // id constraint
    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeId(JackMatchers.equalTo(s2.getScopeId()))
        .execute(db)
    );
    assertEquals(s2, scopes.getScopes().get(0));

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereScopeId(JackMatchers.lessThanOrEqualTo(s2.getScopeId()))
        .execute(db)
    );
    assertEquals(2, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(scopes.getScopes()));
  }

  @Test
  public void testKeyConstraint() throws Exception {
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");
    JsScope s4 = createScope("4");

    transactor.executeAsTransaction(db -> {
      jackStore.scope(s1).indexRecords().put("count0", 15).put("count1", 50).execute(db);
      jackStore.scope(s2).indexRecords().put("count0", 20).put("count1", 60).execute(db);
      jackStore.scope(s3).indexRecords().put("count0", 25).put("count1", 70).execute(db);
      jackStore.scope(s4).indexRecords().put("count0", 30).put("count1", 80).execute(db);
    });

    // single key query
    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.equalTo("15"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), scopes.getScopes());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.notEqualTo("15"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s2, s3, s4), scopes.getScopes());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.between("15", "25"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s1, s2, s3), scopes.getScopes());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.greaterThan("15"))
        .whereRecord("count0", JackMatchers.lessThan("25"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s2), scopes.getScopes());

    // multiple keys query
    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.equalTo("15"))
        .whereRecord("count1", JackMatchers.equalTo("50"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), scopes.getScopes());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.equalTo("15"))
        .whereRecord("count1", JackMatchers.notEqualTo("50"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(0, scopes.size());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes()
        .whereRecord("count0", JackMatchers.greaterThan("15"))
        .whereRecord("count1", JackMatchers.lessThan("80"))
        .orderByScopeName(QueryOrder.ASC)
        .execute(db)
    );
    assertEquals(Lists.newArrayList(s2, s3), scopes.getScopes());
  }

  @Test
  public void testOrder() throws Exception {
    JsScope s1 = createScope("b");
    JsScope s2 = createScope("a");
    JsScope s3 = createScope("c");

    // order by id asc
    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeId(QueryOrder.ASC).execute(db));
    assertEquals(Lists.newArrayList(s1, s2, s3), scopes.getScopes());

    // order by id desc
    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeId(QueryOrder.DESC).execute(db));
    assertEquals(Lists.newArrayList(s3, s2, s1), scopes.getScopes());

    // order by name asc
    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeName(QueryOrder.ASC).execute(db));
    assertEquals(Lists.newArrayList(s2, s1, s3), scopes.getScopes());

    // order by name desc
    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeName(QueryOrder.DESC).execute(db));
    assertEquals(Lists.newArrayList(s3, s1, s2), scopes.getScopes());
  }

  @Test
  public void testLimit() throws Exception {
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeId(QueryOrder.ASC).limit(2).execute(db));
    assertEquals(Lists.newArrayList(s1, s2), scopes.getScopes());

    scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().orderByScopeId(QueryOrder.ASC).limit(1, 2).execute(db));
    assertEquals(Lists.newArrayList(s2, s3), scopes.getScopes());
  }

  @Test
  public void testJson() throws Exception {
    JsonParser parser = new JsonParser();
    JsScope scope1 = createJson(parser, "scope1", "json", "{key1: {key2: [[11]]}}");
    JsScope scope2 = createJson(parser, "scope2", "json", "{key1: {key2: [[12]]}}");
    JsScope scope3 = createJson(parser, "scope3", "json", "{key1: {key2: [[13]]}}");
    JsScope scope4 = createJson(parser, "scope4", "json", "{key1: {key2: 13}}");
    JsScope scope5 = createJson(parser, "scope5", "json", "{key1: {key3: [[13]]}}");
    JsScope scope6 = createJson(parser, "scope6", "json", "{key2: {key1: [[13]]}}");

    JsScopes scopes = transactor.query(db -> jackStore.rootScope().querySubScopes().whereRecord("json.key1.key2", JackMatchers.between("12", "13")).execute(db));
    assertEquals(Lists.newArrayList(scope2, scope3, scope4), scopes.getScopes());
  }

  private JsScope createJson(JsonParser parser, String scope, String key, String jsonString) throws Exception {
    return transactor.query(db -> {
      JsScope newScope = jackStore.rootScope().createSubScope(scope).execute(db);
      jackStore.scope(newScope).indexRecords().putJson(key, parser.parse(jsonString).getAsJsonObject()).execute(db);
      return newScope;
    });
  }

}
