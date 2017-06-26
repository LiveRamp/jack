package com.rapleaf.jack.store.json;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonDbHelper extends BaseJsonTestCase {

  private final JsonParser parser = new JsonParser();
  private String jsonString;

  private void testJson() throws Exception {
    Preconditions.checkNotNull(jsonString);
    JsonObject expected = parser.parse(jsonString).getAsJsonObject();
    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(expected);
    JsonObject actual = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(expected, actual);
    // the number of tuples equal to the number of comma + 1
    Assert.assertEquals(StringUtils.countMatches(jsonString, ",") + 1, jsonDbTuples.size());
    jsonString = null;
  }

  @Test
  public void testString() throws Exception {
    jsonString = "{key: \"string\"}";
    testJson();
  }

  @Test
  public void testLong() throws Exception {
    jsonString = "{k1: 1, k2: 2}";
    testJson();
  }

  @Test
  public void testDouble() throws Exception {
    jsonString = "{k1: 1.1, k2: 2.2, k3: 3.33333}";
    testJson();
  }

  @Test
  public void testBoolean() throws Exception {
    jsonString = "{k1: true, k2: false, k3: true}";
    testJson();
  }

  @Test
  public void testEmptyString() throws Exception {
    jsonString = "{key: \"\"}";
    testJson();

    jsonString = "{key: [\"\", \"\"]}";
    testJson();
  }

  @Test
  public void testNullString() throws Exception {
    jsonString = "{key: \"null\"}";
    testJson();

    jsonString = "{key: [\"null\"]}";
    testJson();
  }

  @Test
  public void testNull() throws Exception {
    jsonString = "{key: null}";
    testJson();

    jsonString = "{key: [null]}";
    testJson();
  }

  @Test
  public void testNullKey() throws Exception {
    jsonString = "{null: value}";
    testJson();
  }

  @Test
  public void testNullStringKey() throws Exception {
    jsonString = "{\"null\": value}";
    testJson();
  }

  @Test
  public void testEmptyKey() throws Exception {
    jsonString = "{\"\": value}";
    testJson();

    jsonString = "{\"\": [1, 2, 3]}";
    testJson();
  }

  @Test
  public void testEmptyArray() throws Exception {
    jsonString = "{key: []}";
    testJson();
  }

  @Test
  public void testEmptyObject() throws Exception {
    jsonString = "{key: {}}";
    testJson();
  }

  @Test
  public void testSimpleObject() throws Exception {
    jsonString = "{object: {k1: v1}, str: value, n1: 1, n2: 2.2, b1: true, b2: false}";
    testJson();
  }

  @Test
  public void testSimpleArray() throws Exception {
    jsonString = "{key: [1.1, 2.2, str, true, false]}";
    testJson();
  }

  @Test
  public void testArrayInArray() throws Exception {
    jsonString = "{key: [[1, 2, 3], [4, 5, 6], 1, 2.2, string, true, false]}";
    testJson();

    jsonString = "{key: [[[[[[1]]]]]]}";
    testJson();

    jsonString = "{key: [1, [2, [3, [4, [5, [6]]]]]]}";
    testJson();

    jsonString = "{key: [1, 2, [3], 4, [[5]], 6]}";
    testJson();
  }

  @Test
  public void testObjectInObject() throws Exception {
    jsonString = "{key: {key: {key: {key: {key: value}}}}}";
    testJson();

    jsonString = "{key0: value, key1: {key0: value, key1: {key0: value, key1: {key0: value, key1: {key0: value, key1: value}}}}}";
    testJson();
  }

  @Test
  public void testObjectInArray() throws Exception {
    jsonString = "{key: [{l1: v1}, {l2: v2}]}";
    testJson();

    jsonString = "{key: [[[[{l1: v1}, {l2: v2}]], {l3: v3}]]}";
    testJson();
  }

  @Test
  public void testComplexObject() throws Exception {
    jsonString = COMPLEX_JSON_STRING;
    testJson();
  }

}
