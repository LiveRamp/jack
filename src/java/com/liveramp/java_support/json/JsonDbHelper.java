package com.liveramp.java_support.json;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonDbHelper {

  private static final String PATH_SEPARATOR = ".";

  public static List<JsonDbTuple> toTupleList(JsonObject json) {
    return toTupleList("", json);
  }

  private static List<JsonDbTuple> toTupleList(String path, JsonObject json) {
    List<JsonDbTuple> tuples = Lists.newArrayList();

    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      String key = entry.getKey();
      JsonElement jsonElement = entry.getValue();

      if (jsonElement.isJsonPrimitive()) {
        tuples.add(new JsonDbTuple(path, key, jsonElement.getAsJsonPrimitive().getAsString()));
      } else if (jsonElement.isJsonArray()) {
        tuples.addAll(toTupleList(path, key, jsonElement.getAsJsonArray()));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(getPath(path, key), jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(new JsonDbTuple(path, key, ""));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  private static List<JsonDbTuple> toTupleList(String path, String key, JsonArray jsonArray) {
    List<JsonDbTuple> tuples = Lists.newArrayListWithCapacity(jsonArray.size());

    for (int i = 0; i < jsonArray.size(); i++) {
      JsonElement jsonElement = jsonArray.get(i);

      if (jsonElement.isJsonPrimitive()) {
        tuples.add(new JsonDbTuple(path, key, jsonElement.getAsJsonPrimitive().getAsString(), i, jsonArray.size()));
      } else if (jsonElement.isJsonArray()) {
        tuples.addAll(toTupleList(path, key, jsonElement.getAsJsonArray()));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(path, jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(new JsonDbTuple(path, key, ""));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  private static String getPath(String path, String key) {
    return path.isEmpty() ? key : path + PATH_SEPARATOR + key;
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonParser parser = new JsonParser();
    JsonObject json = new JsonObject();

    Map<String, Map<String, List<JsonElement>>> arrayBuilder = Maps.newHashMap();
    for (JsonDbTuple tuple : tuples) {
      String path = tuple.getPath();
      String key = tuple.getKey();
      JsonElement parsedValue = parser.parse(tuple.getValue());
      if (parsedValue.isJsonNull()) {
        parsedValue = new JsonPrimitive("");
      }

      if (tuple.getListIndex().isPresent()) {
        if (!arrayBuilder.containsKey(path)) {
          arrayBuilder.put(path, Maps.newHashMap());
        }
        if (!arrayBuilder.get(path).containsKey(key)) {
          arrayBuilder.get(path).put(key, Lists.newArrayList(new JsonElement[tuple.getListSize().get()]));
        }
        arrayBuilder.get(path).get(key).set(tuple.getListIndex().get(), parsedValue);
      } else {
        List<String> split = Lists.newArrayList(path.split(Pattern.quote(PATH_SEPARATOR)));
        JsonObject jsonObject = ensureMapsAndReturnObject(json, split);
        jsonObject.add(key, parsedValue);
      }
    }

    for (Map.Entry<String, Map<String, List<JsonElement>>> entry1 : arrayBuilder.entrySet()) {
      String path = entry1.getKey();
      for (Map.Entry<String, List<JsonElement>> entry2 : entry1.getValue().entrySet()) {
        insertArray(json, path, entry2.getKey(), entry2.getValue());
      }
    }
    return json;
  }

  private static void insertArray(JsonObject json, String path, String key, List<JsonElement> elementList) {
    List<String> split = Lists.newArrayList(path.split(Pattern.quote(PATH_SEPARATOR)));
    JsonObject jsonObject = ensureMapsAndReturnObject(json, split);
    JsonArray array = new JsonArray();
    jsonObject.add(key, array);
    for (JsonElement jsonElement : elementList) {
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
