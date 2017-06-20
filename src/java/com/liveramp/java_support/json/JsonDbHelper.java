package com.liveramp.java_support.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonDbHelper {

  public static List<JsonDbTuple> toTupleList(JsonObject json) {
    return toTupleList(Collections.emptyList(), json);
  }

  private static List<JsonDbTuple> toTupleList(List<TuplePath> parentPaths, JsonObject json) {
    List<JsonDbTuple> tuples = Lists.newArrayList();

    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      String key = entry.getKey();
      JsonElement jsonElement = entry.getValue();

      if (jsonElement.isJsonArray()) {
        tuples.addAll(toTupleList(parentPaths, Optional.of(key), jsonElement.getAsJsonArray()));
        continue;
      }

      childPaths.add(new ElementPath(entry.getKey()));
      if (jsonElement.isJsonPrimitive()) {
        tuples.add(new JsonDbTuple(childPaths, jsonElement.getAsJsonPrimitive().getAsString()));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(childPaths, jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(new JsonDbTuple(childPaths, ""));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  private static List<JsonDbTuple> toTupleList(List<TuplePath> parentPaths, Optional<String> arrayName, JsonArray jsonArray) {
    List<JsonDbTuple> tuples = Lists.newArrayListWithCapacity(jsonArray.size());

    int size = jsonArray.size();
    for (int i = 0; i < jsonArray.size(); i++) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      childPaths.add(new ArrayPath(arrayName, i, size));

      JsonElement jsonElement = jsonArray.get(i);

      if (jsonElement.isJsonPrimitive()) {
        tuples.add(new JsonDbTuple(childPaths, jsonElement.getAsJsonPrimitive().getAsString()));
      } else if (jsonElement.isJsonArray()) {
        JsonArray keylessArray = jsonElement.getAsJsonArray();
        tuples.addAll(toTupleList(childPaths, Optional.empty(), keylessArray));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(childPaths, jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(new JsonDbTuple(childPaths, ""));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  private static String getPath(String path, String key) {
    return path.isEmpty() ? key : path + JsonDbConstants.PATH_SEPARATOR + key;
  }

  private static String getArrayPath(String path, int arrayIndex, int arraySize) {
    return String.format("%s%s%d%s%d", path, JsonDbConstants.LIST_PATH_SEPARATOR, arrayIndex, JsonDbConstants.LIST_PATH_SEPARATOR, arraySize);
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonParser parser = new JsonParser();
    JsonObject json = new JsonObject();

    Map<String, Map<String, List<JsonElement>>> arrayBuilder = Maps.newHashMap();
    for (JsonDbTuple tuple : tuples) {
      String path = tuple.getPath();
      Optional<String> key = tuple.getKey();
      JsonElement parsedValue = parser.parse(tuple.getValue());
      if (parsedValue.isJsonNull()) {
        parsedValue = new JsonPrimitive("");
      }

      if (tuple.isArray()) {
        if (!arrayBuilder.containsKey(path)) {
          arrayBuilder.put(path, Maps.newHashMap());
        }
        if (!arrayBuilder.get(path).containsKey(key.orElse(""))) {
          arrayBuilder.get(path).put(key.orElse(""), Lists.newArrayList(new JsonElement[tuple.getListSize().get()]));
        }
        arrayBuilder.get(path).get(key.orElse("")).set(tuple.getListIndex().get(), parsedValue);
      } else {
        List<String> split = Lists.newArrayList(path.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR)));
        JsonObject jsonObject = ensureMapsAndReturnObject(json, split);
        jsonObject.add(key.orElse(""), parsedValue);
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
    List<String> split = Lists.newArrayList(path.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR)));
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
      String elementName = parseName(split.get(0));
      if (jsonElement == null) {
        json.add(elementName, new JsonObject());
      }
      return ensureMapsAndReturnObject(json.get(elementName).getAsJsonObject(), split.subList(1, split.size()));
    } else {
      return json;
    }
  }

  private static String parseName(String token) {
    String[] splits = token.split(Pattern.quote(JsonDbConstants.LIST_PATH_SEPARATOR));
    if (splits.length > 1) {
      return splits[0];
    } else {
      return token;
    }
  }

}
