package com.rapleaf.jack.store.executors;

import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonParser;
import org.junit.Test;

import com.rapleaf.jack.queries.where_operators.JackMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubScopeInquirer extends BaseExecutorTestCase {

  @Test
  public void testParentScopeId() throws Exception {
    Long parentScopeId = createSubScope(Optional.empty(), Optional.empty());
    jsRecords = transactor.query(db -> {
      for (int i = 0; i < 5; ++i) {
        jackStore2.scope(parentScopeId).createSubScope().execute(db);
      }
      return jackStore2.scope(parentScopeId).querySubScopes().execute(db);
    });
    assertEquals(parentScopeId, jsRecords.getParentScopeId());
  }

  @Test
  public void testNoConstraint() throws Exception {
    jsRecords = transactor.query(db -> jackStore2.rootScope().querySubScopes().execute(db));
    assertTrue(jsRecords.isEmpty());

    // two scopes under root
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));

    jsRecords = transactor.query(db -> jackStore2.rootScope().querySubScopes().execute(db));
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getScopeIds()));

    // three scopes under scope 1
    long s01 = createSubScope(Optional.of(s1), Optional.of("1"));
    long s02 = createSubScope(Optional.of(s1), Optional.of("2"));
    long s03 = createSubScope(Optional.of(s1), Optional.of("3"));

    jsRecords = transactor.query(db -> jackStore2.rootScope().querySubScopes().execute(db));
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getScopeIds()));

    jsRecords = transactor.query(db -> jackStore2.scope(s1).querySubScopes().execute(db));
    assertEquals(3, jsRecords.size());
    assertEquals(Sets.newHashSet(s01, s02, s03), Sets.newHashSet(jsRecords.getScopeIds()));
  }

  @Test
  public void testScopeConstraint() throws Exception {
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));
    long s3 = createSubScope(Optional.empty(), Optional.of("3"));

    // name constraint
    jsRecords = transactor.query(db ->
        jackStore2.rootScope()
            .querySubScopes()
            .whereSubScopeName(JackMatchers.greaterThan("2"))
            .execute(db)
    );
    assertEquals(1, jsRecords.size());
    assertEquals(s3, jsRecords.getScopeIds().get(0).longValue());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubScopeName(JackMatchers.greaterThanOrEqualTo("2"))
            .execute(db)
    );
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s2, s3), Sets.newHashSet(jsRecords.getScopeIds()));

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubScopeName(JackMatchers.isNull())
            .execute(db)
    );
    assertTrue(jsRecords.isEmpty());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubScopeName(JackMatchers.equalTo("2"))
            .execute(db)
    );
    assertEquals(s2, jsRecords.getScopeIds().get(0).longValue());

    // id constraint
    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubScopeId(JackMatchers.equalTo(s2))
            .execute(db)
    );
    assertEquals(s2, jsRecords.getScopeIds().get(0).longValue());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubScopeId(JackMatchers.lessThanOrEqualTo(s2))
            .execute(db)
    );
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getScopeIds()));
  }

  @Test
  public void testKeyConstraint() throws Exception {
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));
    long s3 = createSubScope(Optional.empty(), Optional.of("3"));
    long s4 = createSubScope(Optional.empty(), Optional.of("4"));

    transactor.executeAsTransaction(db -> {
      jackStore2.scope(s1).update().put("count0", 15).put("count1", 50).execute(db);
      jackStore2.scope(s2).update().put("count0", 20).put("count1", 60).execute(db);
      jackStore2.scope(s3).update().put("count0", 25).put("count1", 70).execute(db);
      jackStore2.scope(s4).update().put("count0", 30).put("count1", 80).execute(db);
    });

    // single key query
    jsRecords = transactor.query(db ->
        jackStore2.rootScope()
            .querySubScopes()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), jsRecords.getScopeIds());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.notEqualTo("15"))
            .execute(db)
    );

    assertEquals(Lists.newArrayList(s2, s3, s4), jsRecords.getScopeIds());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.between("15", "25"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1, s2, s3), jsRecords.getScopeIds());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.greaterThan("15"))
            .whereSubRecord("count0", JackMatchers.lessThan("25"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s2), jsRecords.getScopeIds());

    // multiple keys query
    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .whereSubRecord("count1", JackMatchers.equalTo("50"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), jsRecords.getScopeIds());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .whereSubRecord("count1", JackMatchers.notEqualTo("50"))
            .execute(db)
    );
    assertEquals(0, jsRecords.size());

    jsRecords = transactor.query(db ->
        jackStore2.rootScope().querySubScopes()
            .whereSubRecord("count0", JackMatchers.greaterThan("15"))
            .whereSubRecord("count1", JackMatchers.lessThan("80"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s2, s3), jsRecords.getScopeIds());
  }

  @Test
  public void testJson() throws Exception {
    JsonParser parser = new JsonParser();
    long scope1 = createJson(parser, "scope1", "json", "{key1: {key2: [[11]]}}");
    long scope2 = createJson(parser, "scope2", "json", "{key1: {key2: [[12]]}}");
    long scope3 = createJson(parser, "scope3", "json", "{key1: {key2: [[13]]}}");
    long scope4 = createJson(parser, "scope4", "json", "{key1: {key2: 13}}");
    long scope5 = createJson(parser, "scope5", "json", "{key1: {key3: [[13]]}}");
    long scope6 = createJson(parser, "scope6", "json", "{key2: {key1: [[13]]}}");

    jsRecords = transactor.query(db -> jackStore2.rootScope().querySubScopes().whereSubRecord("json.key1.key2", JackMatchers.between("12", "13")).execute(db));
    assertEquals(Lists.newArrayList(scope2, scope3, scope4), jsRecords.getScopeIds());
  }

  private long createJson(JsonParser parser, String subScopeName, String key, String jsonString) throws Exception {
    return transactor.query(db -> {
      long newSubScopeId = createSubScope(Optional.empty(), Optional.of(subScopeName));
      jackStore2.scope(newSubScopeId).update().putJson(key, parser.parse(jsonString).getAsJsonObject()).execute(db);
      return newSubScopeId;
    });
  }

}
