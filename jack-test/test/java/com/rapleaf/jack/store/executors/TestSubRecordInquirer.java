package com.rapleaf.jack.store.executors;

import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonParser;
import org.junit.Test;

import com.rapleaf.jack.queries.where_operators.JackMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubRecordInquirer extends BaseExecutorTestCase {

  @Test
  public void testParentScopeId() throws Exception {
    Long parentScopeId = createSubScope(Optional.empty(), Optional.empty());
    jsRecords = transactor.query(db -> {
      for (int i = 0; i < 5; ++i) {
        jackStore.record(parentScopeId).createSubRecord().execute(db);
      }
      return jackStore.record(parentScopeId).querySubRecords().execute(db);
    });
    assertEquals(parentScopeId, jsRecords.getParentRecordId());
  }

  @Test
  public void testNoConstraint() throws Exception {
    jsRecords = transactor.query(db -> jackStore.rootRecord().querySubRecords().execute(db));
    assertTrue(jsRecords.isEmpty());

    // two records under root
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));

    jsRecords = transactor.query(db -> jackStore.rootRecord().querySubRecords().execute(db));
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getRecordIds()));

    // three records under record 1
    long s01 = createSubScope(Optional.of(s1), Optional.of("1"));
    long s02 = createSubScope(Optional.of(s1), Optional.of("2"));
    long s03 = createSubScope(Optional.of(s1), Optional.of("3"));

    jsRecords = transactor.query(db -> jackStore.rootRecord().querySubRecords().execute(db));
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getRecordIds()));

    jsRecords = transactor.query(db -> jackStore.record(s1).querySubRecords().execute(db));
    assertEquals(3, jsRecords.size());
    assertEquals(Sets.newHashSet(s01, s02, s03), Sets.newHashSet(jsRecords.getRecordIds()));
  }

  @Test
  public void testScopeConstraint() throws Exception {
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));
    long s3 = createSubScope(Optional.empty(), Optional.of("3"));

    // name constraint
    jsRecords = transactor.query(db ->
        jackStore.rootRecord()
            .querySubRecords()
            .whereSubRecordName(JackMatchers.greaterThan("2"))
            .execute(db)
    );
    assertEquals(1, jsRecords.size());
    assertEquals(s3, jsRecords.getRecordIds().get(0).longValue());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecordName(JackMatchers.greaterThanOrEqualTo("2"))
            .execute(db)
    );
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s2, s3), Sets.newHashSet(jsRecords.getRecordIds()));

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecordName(JackMatchers.isNull())
            .execute(db)
    );
    assertTrue(jsRecords.isEmpty());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecordName(JackMatchers.equalTo("2"))
            .execute(db)
    );
    assertEquals(s2, jsRecords.getRecordIds().get(0).longValue());

    // id constraint
    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecordId(JackMatchers.equalTo(s2))
            .execute(db)
    );
    assertEquals(s2, jsRecords.getRecordIds().get(0).longValue());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecordId(JackMatchers.lessThanOrEqualTo(s2))
            .execute(db)
    );
    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(jsRecords.getRecordIds()));
  }

  @Test
  public void testKeyConstraint() throws Exception {
    long s1 = createSubScope(Optional.empty(), Optional.of("1"));
    long s2 = createSubScope(Optional.empty(), Optional.of("2"));
    long s3 = createSubScope(Optional.empty(), Optional.of("3"));
    long s4 = createSubScope(Optional.empty(), Optional.of("4"));

    long s11 = createSubScope(Optional.of(s1), Optional.of("sub record of 1"));
    long s21 = createSubScope(Optional.of(s2), Optional.of("sub record of 2"));
    long s31 = createSubScope(Optional.of(s3), Optional.of("sub record of 3"));
    long s41 = createSubScope(Optional.of(s4), Optional.of("sub record of 4"));

    transactor.executeAsTransaction(db -> {
      jackStore.record(s1).update().put("count0", 15).put("count1", 50).exec(db);
      jackStore.record(s2).update().put("count0", 20).put("count1", 60).exec(db);
      jackStore.record(s3).update().put("count0", 25).put("count1", 70).exec(db);
      jackStore.record(s4).update().put("count0", 30).put("count1", 80).exec(db);

      // sub records should not be included in query result
      jackStore.record(s11).update().put("count0", 15).put("count1", 50).exec(db);
      jackStore.record(s21).update().put("count0", 20).put("count1", 60).exec(db);
      jackStore.record(s31).update().put("count0", 25).put("count1", 70).exec(db);
      jackStore.record(s41).update().put("count0", 30).put("count1", 80).exec(db);
    });

    // single key query
    jsRecords = transactor.query(db ->
        jackStore.rootRecord()
            .querySubRecords()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), jsRecords.getRecordIds());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.notEqualTo("15"))
            .execute(db)
    );

    assertEquals(Lists.newArrayList(s2, s3, s4), jsRecords.getRecordIds());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.between("15", "25"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1, s2, s3), jsRecords.getRecordIds());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.greaterThan("15"))
            .whereSubRecord("count0", JackMatchers.lessThan("25"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s2), jsRecords.getRecordIds());

    // multiple keys query
    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .whereSubRecord("count1", JackMatchers.equalTo("50"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s1), jsRecords.getRecordIds());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.equalTo("15"))
            .whereSubRecord("count1", JackMatchers.notEqualTo("50"))
            .execute(db)
    );
    assertEquals(0, jsRecords.size());

    jsRecords = transactor.query(db ->
        jackStore.rootRecord().querySubRecords()
            .whereSubRecord("count0", JackMatchers.greaterThan("15"))
            .whereSubRecord("count1", JackMatchers.lessThan("80"))
            .execute(db)
    );
    assertEquals(Lists.newArrayList(s2, s3), jsRecords.getRecordIds());
  }

  @Test
  public void testJson() throws Exception {
    JsonParser parser = new JsonParser();
    long r1 = createJson(parser, "record1", "json", "{key1: {key2: [[11]]}}");
    long r2 = createJson(parser, "record2", "json", "{key1: {key2: [[12]]}}");
    long r3 = createJson(parser, "record3", "json", "{key1: {key2: [[13]]}}");
    long r4 = createJson(parser, "record4", "json", "{key1: {key2: 13}}");
    long r5 = createJson(parser, "record5", "json", "{key1: {key3: [[13]]}}");
    long r6 = createJson(parser, "record6", "json", "{key2: {key1: [[13]]}}");

    jsRecords = transactor.query(db -> jackStore.rootRecord().querySubRecords().whereSubRecord("json.key1.key2", JackMatchers.between("12", "13")).execute(db));
    assertEquals(Lists.newArrayList(r2, r3, r4), jsRecords.getRecordIds());
  }

  @Test
  public void testKeySelection() throws Exception {
    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    transactor.execute(db -> {
      long subRecordId = jackStore.rootRecord().createSubRecord()
          .put(key1, 1L)
          .put(key2, 2L)
          .put(key3, 3L)
          .exec(db);

      // when no key is specified, all keys exist in js record
      jsRecords = jackStore.rootRecord().querySubRecords()
          .whereSubRecordId(JackMatchers.equalTo(subRecordId))
          .execute(db);
      assertEquals(Sets.newHashSet(key1, key2, key3), jsRecords.getOnly().keySet());

      // when keys are specified, only selected keys exist in js record
      jsRecords = jackStore.rootRecord().querySubRecords()
          .whereSubRecordId(JackMatchers.equalTo(subRecordId))
          .selectKey(key1, key3)
          .execute(db);
      assertEquals(Sets.newHashSet(key1, key3), jsRecords.getOnly().keySet());
    });
  }

  private long createJson(JsonParser parser, String subScopeName, String key, String jsonString) throws Exception {
    return transactor.query(db -> {
      long newSubScopeId = createSubScope(Optional.empty(), Optional.of(subScopeName));
      jackStore.record(newSubScopeId).update().putJson(key, parser.parse(jsonString).getAsJsonObject()).execute(db);
      return newSubScopeId;
    });
  }

}
