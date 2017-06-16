package com.rapleaf.jack.store.json;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonDbHelper {

  @Test
  public void testRoundTrip() {
    String json = "{" +
        "'key1' : 'val1'," +
        "'key2' : " +
        "  {" +
        "    'array1' : [1, 2, 3]," +
        "    'array2' : ['four', 'five']," +
        "    'key3' : 'val2'," +
        "    'deepObject':" +
        "       {" +
        "        'deepKey' : 0.54," +
        "        'deepArray' : [false,true,false]" +
        "       }" +
        "  }," +
        "'array3' : [6,7,8,9,10]," +
        "'bool1' : true" +
        "}";

    JsonParser parser = new JsonParser();
    JsonObject parse = parser.parse(json).getAsJsonObject();

    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(parse);
    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonDbTuples);
    Assert.assertEquals(parse, jsonObject);
  }

}
