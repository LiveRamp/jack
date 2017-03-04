package com.liveramp.java_support.json;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJsonDbHelper {

  private static Logger LOG = LoggerFactory.getLogger(TestJsonDbHelper.class);

  @Test
  public void testRoundTrip() {
    String json = "{ 'key1':'val1', 'key2' : { 'array1':[1,2,3], 'array2':['four','five'], 'key3' : 'val2', " +
        "'deepObject' : {'deepKey':0.54, 'deepArray': [false,true,false]}}, 'array3':[6,7,8,9,10], 'bool1':true}";

    JsonParser parser = new JsonParser();
    JsonObject parse = parser.parse(json).getAsJsonObject();

    List<JsonDbTuple> jsonDbTuples = JsonDbHelper.toTupleList(parse);
    System.out.println("tuples: "+jsonDbTuples);
    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonDbTuples);

    Assert.assertEquals(parse, jsonObject);
  }
}
