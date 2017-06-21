package com.rapleaf.jack.store.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang.math.NumberUtils;

public class JsonDbHelper {

  public static List<JsonDbTuple> toTupleList(JsonObject json) {
    return toTupleList(Collections.emptyList(), json);
  }

  public static List<JsonDbTuple> toTupleList(List<TuplePath> parentPaths, JsonObject json) {
    List<JsonDbTuple> tuples = Lists.newArrayList();

    Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
    if (entrySet.size() == 0) {
      tuples.add(JsonDbTuple.createEmptyObject(parentPaths));
    }

    for (Map.Entry<String, JsonElement> entry : entrySet) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      String key = entry.getKey();
      JsonElement jsonElement = entry.getValue();

      if (jsonElement.isJsonArray()) {
        tuples.addAll(getTupleListFromArray(parentPaths, Optional.of(key), jsonElement.getAsJsonArray()));
        continue;
      }

      childPaths.add(new ElementPath(entry.getKey()));
      if (jsonElement.isJsonPrimitive()) {
        tuples.add(JsonDbTuple.create(childPaths, jsonElement.getAsJsonPrimitive().getAsString()));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(childPaths, jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(JsonDbTuple.createNull(childPaths));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  private static List<JsonDbTuple> getTupleListFromArray(List<TuplePath> parentPaths, Optional<String> arrayName, JsonArray jsonArray) {
    List<JsonDbTuple> tuples = Lists.newArrayListWithCapacity(jsonArray.size());
    int size = jsonArray.size();

    if (size == 0) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      childPaths.add(new ArrayPath(arrayName, 0, 0));
      tuples.add(JsonDbTuple.createEmptyObject(childPaths));
    }

    for (int i = 0; i < jsonArray.size(); i++) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      childPaths.add(new ArrayPath(arrayName, i, size));

      JsonElement jsonElement = jsonArray.get(i);

      if (jsonElement.isJsonPrimitive()) {
        tuples.add(JsonDbTuple.create(childPaths, jsonElement.getAsJsonPrimitive().getAsString()));
      } else if (jsonElement.isJsonArray()) {
        JsonArray keylessArray = jsonElement.getAsJsonArray();
        tuples.addAll(getTupleListFromArray(childPaths, Optional.empty(), keylessArray));
      } else if (jsonElement.isJsonObject()) {
        tuples.addAll(toTupleList(childPaths, jsonElement.getAsJsonObject()));
      } else if (jsonElement.isJsonNull()) {
        tuples.add(JsonDbTuple.createNull(childPaths));
      } else {
        throw new IllegalArgumentException("Unexpected json element: " + jsonElement);
      }
    }

    return tuples;
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonObject json = new JsonObject();
    for (JsonDbTuple tuple : tuples) {
      processTuple(json, tuple.getPaths(), tuple.getValue());
    }
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
      // when the child has a name, the parent must be an object
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

      if (!tailPaths.isEmpty()) {
        addChildPathToParentArray(childArray, childIndex.get(), tailPaths, value);
      } else if (value != null) {
        Preconditions.checkState(childArray.size() == childIndex.get());
        childArray.add(getJsonElement(value));
      } else {
        Preconditions.checkState(childArray.size() == 0);
      }
    } else {
      // when the child has no name, the parent must be an array
      Preconditions.checkState(parentElement.isJsonArray());
      JsonArray parentArray = parentElement.getAsJsonArray();

      if (!tailPaths.isEmpty()) {
        addChildPathToParentArray(parentArray, childIndex.get(), tailPaths, value);
      } else if (value != null) {
        Preconditions.checkState(parentArray.size() == childIndex.get());
        parentArray.add(getJsonElement(value));
      } else {
        Preconditions.checkState(parentArray.size() == 0);
      }
    }
  }

  private static void addElementPath(JsonElement parentElement, ElementPath childPath, List<TuplePath> tailPaths, String value) {
    Optional<String> childName = childPath.getName();
    Preconditions.checkState(childName.isPresent());

    final JsonObject parentObject;
    if (parentElement.isJsonArray()) {
      parentObject = new JsonObject();
      parentElement.getAsJsonArray().add(parentObject);
    } else {
      parentObject = parentElement.getAsJsonObject();
    }

    if (!tailPaths.isEmpty()) {
      addChildPathToParentObject(parentObject, childName.get(), tailPaths, value);
    } else if (value != null) {
      parentObject.add(childName.get(), getJsonElement(value));
    } else {
      parentObject.add(childName.get(), new JsonObject());
    }
  }

  private static void addChildPathToParentObject(JsonObject parentObject, String childPathName, List<TuplePath> tailPaths, String value) {
    TuplePath nextChildPath = tailPaths.get(0);
    final JsonElement childElement;
    if (!parentObject.has(childPathName)) {
      if (nextChildPath.getName().isPresent() || !nextChildPath.isArray()) {
        childElement = new JsonObject();
      } else {
        childElement = new JsonArray();
      }
      parentObject.add(childPathName, childElement);
    } else {
      childElement = parentObject.get(childPathName);
    }
    processTuple(childElement, tailPaths, value);
  }

  private static void addChildPathToParentArray(JsonArray parentArray, int childPathIndex, List<TuplePath> tailPaths, String value) {
    TuplePath nextChildPath = tailPaths.get(0);
    final JsonElement childElement;
    if (parentArray.size() <= childPathIndex) {
      if (nextChildPath.getName().isPresent() || !nextChildPath.isArray()) {
        childElement = new JsonObject();
      } else {
        childElement = new JsonArray();
      }
      parentArray.add(childElement);
    } else {
      childElement = parentArray.get(childPathIndex);
    }
    processTuple(childElement, tailPaths, value);
  }

  private static JsonElement getJsonElement(String value) {
    if (JsonDbConstants.NULL_VALUE.equals(value)) {
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
