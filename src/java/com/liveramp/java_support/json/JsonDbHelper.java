package com.liveramp.java_support.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.math.NumberUtils;

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
        tuples.add(new JsonDbTuple(childPaths, null));
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
        tuples.add(new JsonDbTuple(childPaths, null));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonObject json = new JsonObject();
    tuples.forEach(tuple -> processTuple(json, tuple.getPaths(), tuple.getValue()));
    return json;
  }

  private static void processTuple(JsonElement parentElement, List<TuplePath> paths, String value) {
    Preconditions.checkArgument(!paths.isEmpty());
    TuplePath childPath = paths.get(0);
    if (childPath.isArray()) {
      addArrayPath(parentElement, (ArrayPath)childPath, paths.subList(1, paths.size()), value);
    } else {
      addElementPath(parentElement, (ElementPath)childPath, paths.subList(1, paths.size()), value);
    }
  }

  private static void addArrayPath(JsonElement parentElement, ArrayPath childPath, List<TuplePath> tailPaths, String value) {
    Optional<String> childName = childPath.getName();
    Optional<Integer> childIndex = childPath.getListIndex();
    Optional<Integer> childSize = childPath.getListSize();
    Preconditions.checkState(childIndex.isPresent());
    Preconditions.checkState(childSize.isPresent());

    if (childName.isPresent()) {
      String name = childName.get();
      Preconditions.checkState(parentElement.isJsonObject());

      JsonObject parentObject = parentElement.getAsJsonObject();
      final JsonArray childArray;

      if (!parentObject.has(name)) {
        childArray = new JsonArray();
        parentObject.add(name, childArray);
      } else {
        childArray = parentObject.get(name).getAsJsonArray();
      }

      if (tailPaths.isEmpty()) {
        Preconditions.checkState(childArray.size() == childIndex.get());
        childArray.add(getJsonElement(value));
      } else {
        TuplePath nextChildPath = tailPaths.get(0);
        final JsonElement childElement;
        if (childArray.size() <= childIndex.get()) {
          if (nextChildPath.getName().isPresent() || !nextChildPath.isArray()) {
            childElement = new JsonObject();
          } else {
            childElement = new JsonArray();
          }
          childArray.add(childElement);
        } else {
          childElement = childArray.get(childIndex.get());
        }
        processTuple(childElement, tailPaths, value);
      }
    } else {
      Preconditions.checkState(parentElement.isJsonArray());
      JsonArray parentArray = parentElement.getAsJsonArray();

      if (tailPaths.isEmpty()) {
        Preconditions.checkState(parentArray.size() == childIndex.get());
        parentArray.add(getJsonElement(value));
      } else {
        processTuple(parentArray, tailPaths, value);
      }
    }
  }

  private static void addElementPath(JsonElement parentElement, ElementPath childPath, List<TuplePath> tailPaths, String value) {
    Optional<String> childName = childPath.getName();
    Preconditions.checkState(childName.isPresent());

    JsonObject parentObject = parentElement.getAsJsonObject();

    if (tailPaths.isEmpty()) {
      parentObject.add(childName.get(), getJsonElement(value));
    } else {
      TuplePath nextChildPath = tailPaths.get(0);
      final JsonElement childElement;
      if (!parentObject.has(childName.get())) {
        if (nextChildPath.getName().isPresent() || !nextChildPath.isArray()) {
          childElement = new JsonObject();
        } else {
          childElement = new JsonArray();
        }
        parentObject.add(childName.get(), childElement);
      } else {
        childElement = parentObject.get(childName.get());
      }
      processTuple(childElement, tailPaths, value);
    }
  }

  private static JsonElement getJsonElement(String value) {
    if (value == null) {
      return JsonNull.INSTANCE;
    }

    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
      return new JsonPrimitive(Boolean.valueOf(value));
    }

    if (NumberUtils.isNumber(value)) {
      try {
        return new JsonPrimitive(Long.valueOf(value));
      } catch (NumberFormatException e) {
        // ignore
      }
      try {
        return new JsonPrimitive(Double.valueOf(value));
      } catch (NumberFormatException e) {
        // ignore;
      }
    }

    return new JsonPrimitive(value);
  }

}
