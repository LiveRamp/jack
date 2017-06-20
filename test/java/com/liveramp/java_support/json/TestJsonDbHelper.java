package com.liveramp.java_support.json;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonDbHelper {

  @Test
  public void testArray() throws Exception {
    JsonParser parser = new JsonParser();
    String json = "{\"array\" : [[1, 2, 3], [4, 5, 6]]}";

    JsonObject expected = parser.parse(json).getAsJsonObject();
    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(expected);
    System.out.println(Joiner.on("\n").join(jsonDbTuples));
    JsonObject actual = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNestedArray() throws Exception {
    JsonParser parser = new JsonParser();
    String json = "{\"key\": [[1, 2, 3], [4, 5, 6]]}";

    JsonObject expected = parser.parse(json).getAsJsonObject();
    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(expected);
    System.out.println(Joiner.on("\n").join(jsonDbTuples));
    JsonObject actual = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testNestedObject() throws Exception {
    JsonParser parser = new JsonParser();
    String json = "{\"key\": [{\"l1\" : \"v1\"}, {\"l2\" : \"v2\"}]}";

    JsonObject expected = parser.parse(json).getAsJsonObject();
    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(expected);
    System.out.println(Joiner.on("\n").join(jsonDbTuples));
    JsonObject actual = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testRoundTrip() throws Exception {
    String json = "{" +
        "\"key1\" : \"val1\"," +
        "\"key2\" :" +
        "  {" +
        "    \"array1\" : [1, 2, 3]," +
        "    \"array2\" : [\"four\", \"five\"]," +
        "    \"key3\" : \"val2\"," +
        "    \"deepObject\":" +
        "       {" +
        "        \"deepKey\" : 0.54," +
        "        \"deepArray\" : [false, true, false]" +
        "       }" +
        "  }," +
        "\"arraysInArray\" :" +
        "  [" +
        "    [1, 2, 3]," +
        "    [4, 5, 6]," +
        "    [7, 8, 9]" +
        "  ]," +
        "\"objectsInArray\" :" +
        "  [" +
        "    {" +
        "      \"k1\" : [true, false, true]," +
        "      \"k2\" : 11" +
        "    }," +
        "    {" +
        "      \"k1\" : [true, false, true]," +
        "      \"k2\" : 12" +
        "    }" +
        "  ]," +
        "\"array3\" : [6,7,8,9,10]," +
        "\"bool1\" : true," +
        "\"bool2\" : false," +
        "\"nullKey\" : \"null\"" +
        "}";

    JsonParser parser = new JsonParser();
    JsonObject expected = parser.parse(json).getAsJsonObject();
    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(expected);
    System.out.println(Joiner.on("\n").join(jsonDbTuples));
    JsonObject actual = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(expected, actual);
  }
}
