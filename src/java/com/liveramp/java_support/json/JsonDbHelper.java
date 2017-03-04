package com.liveramp.java_support.json;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import com.liveramp.commons.collections.nested_map.TwoNestedMap;

public class JsonDbHelper {


  public static final String SEPERATOR = ".";

  public static List<JsonDbTuple> toTupleList(JsonObject json) {
    return toTupleList("", json);
  }

  private static List<JsonDbTuple> toTupleList(String path, JsonObject json) {
    List<JsonDbTuple> result = Lists.newArrayList();
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      if (entry.getValue().isJsonPrimitive()) {
        result.add(new JsonDbTuple(path, entry.getKey(), entry.getValue().getAsJsonPrimitive().getAsString()));
      } else if (entry.getValue().isJsonArray()) {
        handleJsonArray(path, result, entry);
      } else if (entry.getValue().isJsonObject()) {
        result.addAll(toTupleList(getPath(path, entry), entry.getValue().getAsJsonObject()));
      } else {
        throw new IllegalStateException("Unexpected JsonElement " + entry.getValue());
      }
    }
    return result;
  }

  private static void handleJsonArray(String path, List<JsonDbTuple> result, Map.Entry<String, JsonElement> entry) {
    JsonArray ja = entry.getValue().getAsJsonArray();
    for (int i = 0; i < ja.size(); i++) {
      JsonElement el = ja.get(i);
      if (el.isJsonPrimitive()) {
        result.add(new JsonDbTuple(path, entry.getKey(), el.getAsJsonPrimitive().getAsString(), i, ja.size()));
      }
    }
  }

  @NotNull
  private static String getPath(String path, Map.Entry<String, JsonElement> entry) {
    return path.isEmpty() ? entry.getKey() : path + SEPERATOR + entry.getKey();
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonParser parser = new JsonParser();
    JsonObject json = new JsonObject();
    TwoNestedMap<String, String, List<JsonElement>> arrayBuilder = new TwoNestedMap<>();
    for (JsonDbTuple tuple : tuples) {
      String path = tuple.getPath();
      String key = tuple.getKey();
      JsonElement parsedValue = parser.parse(tuple.getValue());

      if (tuple.getListIndex().isPresent()) {
        if (!arrayBuilder.containsKey(path, key)) {
          arrayBuilder.put(path, key, Lists.newArrayList(new JsonElement[tuple.getListSize().get()]));
        }
        arrayBuilder.get(path, key).set(tuple.getListIndex().get(), parsedValue);
      } else {
        List<String> split = Lists.newArrayList(path.split(Pattern.quote(SEPERATOR)));
        JsonObject jsonObject = ensureMapsAndReturnObject(json, split);
        jsonObject.add(key, parsedValue);
      }
    }

    for (TwoNestedMap.Entry<String, String, List<JsonElement>> entries : arrayBuilder.entrySet()) {
      insertArray(json, entries);
    }
    return json;
  }

  private static void insertArray(JsonObject json, TwoNestedMap.Entry<String, String, List<JsonElement>> entries) {
    List<String> split = Lists.newArrayList(entries.getK1().split(Pattern.quote(SEPERATOR)));
    JsonObject jsonObject = ensureMapsAndReturnObject(json, split);
    JsonArray array = new JsonArray();
    jsonObject.add(entries.getK2(), array);
    for (JsonElement jsonElement : entries.getValue()) {
      array.add(jsonElement);
    }
  }

  private static JsonObject ensureMapsAndReturnObject(JsonObject json, List<String> split) {
    if (!split.isEmpty() && !split.get(0).isEmpty()) {
      JsonElement jsonElement = json.get(split.get(0));
      if (jsonElement == null) {
        json.add(split.get(0), new JsonObject());
      }
      return ensureMapsAndReturnObject(json.get(split.get(0)).getAsJsonObject(), split.subList(1, split.size()));
    } else {
      return json;
    }
  }
}