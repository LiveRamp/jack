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

import com.rapleaf.jack.store.ValueType;

public final class JsonDbHelper {

  private JsonDbHelper() {
  }

  public static List<JsonDbTuple> toTupleList(JsonObject json) {
    return toTupleList(Collections.emptyList(), json);
  }

  public static List<JsonDbTuple> toTupleList(List<TuplePath> parentPaths, JsonObject json) {
    List<JsonDbTuple> tuples = Lists.newArrayList();

    Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
    if (entrySet.size() == 0) {
      tuples.add(JsonDbTuple.createEmpty(parentPaths));
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
        tuples.add(createPrimitiveTuple(childPaths, jsonElement.getAsJsonPrimitive()));
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
      tuples.add(JsonDbTuple.createEmpty(childPaths));
    }

    for (int i = 0; i < jsonArray.size(); i++) {
      List<TuplePath> childPaths = Lists.newArrayList(parentPaths);
      childPaths.add(new ArrayPath(arrayName, i, size));

      JsonElement jsonElement = jsonArray.get(i);

      if (jsonElement.isJsonPrimitive()) {
        tuples.add(createPrimitiveTuple(childPaths, jsonElement.getAsJsonPrimitive()));
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

  private static JsonDbTuple createPrimitiveTuple(List<TuplePath> childPaths, JsonPrimitive jsonPrimitive) {
    if (jsonPrimitive.isBoolean()) {
      return JsonDbTuple.createBoolean(childPaths, jsonPrimitive.getAsString());
    } else if (jsonPrimitive.isNumber()) {
      return JsonDbTuple.createNumber(childPaths, jsonPrimitive.getAsString());
    } else {
      return JsonDbTuple.createString(childPaths, jsonPrimitive.getAsString());
    }
  }

  public static JsonObject fromTupleList(List<JsonDbTuple> tuples) {
    JsonObject json = new JsonObject();
    for (JsonDbTuple tuple : tuples) {
      processTuple(json, tuple.getPaths(), tuple.getType(), tuple.getValue());
    }
    return json;
  }

  private static void processTuple(JsonElement parentElement, List<TuplePath> paths, ValueType type, String value) {
    Preconditions.checkArgument(!paths.isEmpty());
    TuplePath childPath = paths.get(0);
    if (childPath.isArray()) {
      addArrayPath(parentElement, (ArrayPath)childPath, paths.subList(1, paths.size()), type, value);
    } else {
      addElementPath(parentElement, (ElementPath)childPath, paths.subList(1, paths.size()), type, value);
    }
  }

  private static void addArrayPath(JsonElement parentElement, ArrayPath childPath, List<TuplePath> tailPaths, ValueType type, String value) {
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
        addChildPathToParentArray(childArray, childIndex.get(), tailPaths, type, value);
      } else {
        // tuples are sorted by name, and this element must have not been added to the array
        Preconditions.checkState(childArray.size() == childIndex.get());
        if (type != ValueType.JSON_EMPTY) {
          childArray.add(getJsonElement(type, value));
        } else if (childSize.get() > 0) {
          // when tail paths is empty and child size is not zero,
          // it is an empty object inside an array: [{}]
          childArray.add(new JsonObject());
          // otherwise, it is just an empty array and can be ignored: []
        }
      }
    } else {
      // when the child has no name, the parent must be an array
      Preconditions.checkState(parentElement.isJsonArray());
      JsonArray parentArray = parentElement.getAsJsonArray();

      if (!tailPaths.isEmpty()) {
        addChildPathToParentArray(parentArray, childIndex.get(), tailPaths, type, value);
      } else {
        // tuples are sorted by name, and this element must have not been added to the array
        Preconditions.checkState(parentArray.size() == childIndex.get());
        if (type != ValueType.JSON_EMPTY) {
          parentArray.add(getJsonElement(type, value));
        }
      }
    }
  }

  private static void addElementPath(JsonElement parentElement, ElementPath childPath, List<TuplePath> tailPaths, ValueType type, String value) {
    // parent element must be an object because it has an element path
    Preconditions.checkState(parentElement.isJsonObject());
    // child path must have a name because it is an element path
    Optional<String> childName = childPath.getName();
    Preconditions.checkState(childName.isPresent());

    final JsonObject parentObject = parentElement.getAsJsonObject();
    if (!tailPaths.isEmpty()) {
      addElementChildPathToParentObject(parentObject, childName.get(), tailPaths, type, value);
    } else {
      parentObject.add(childName.get(), getJsonElement(type, value));
    }
  }

  private static void addElementChildPathToParentObject(JsonObject parentObject, String childPathName, List<TuplePath> tailPaths, ValueType type, String value) {
    TuplePath nextChildPath = tailPaths.get(0);
    final JsonElement childElement;
    if (!parentObject.has(childPathName)) {
      // current child must be an object because it is an element path
      childElement = new JsonObject();
      // next child must have a name because the current child is an object
      Preconditions.checkArgument(nextChildPath.getName().isPresent());
      parentObject.add(childPathName, childElement);
    } else {
      childElement = parentObject.get(childPathName);
    }
    processTuple(childElement, tailPaths, type, value);
  }

  private static void addChildPathToParentArray(JsonArray parentArray, int childPathIndex, List<TuplePath> tailPaths, ValueType type, String value) {
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
    processTuple(childElement, tailPaths, type, value);
  }

  private static JsonElement getJsonElement(ValueType type, String value) {
    switch (type) {
      case JSON_STRING:
        return new JsonPrimitive(value);
      case JSON_BOOLEAN:
        return new JsonPrimitive(Boolean.valueOf(value));
      case JSON_NUMBER:
        try {
          return new JsonPrimitive(Long.valueOf(value));
        } catch (NumberFormatException e1) {
          try {
            return new JsonPrimitive(Double.valueOf(value));
          } catch (NumberFormatException e2) {
            throw new IllegalArgumentException("Unexpected json number: " + value);
          }
        }
      case JSON_NULL:
        return JsonNull.INSTANCE;
      case JSON_EMPTY:
        return new JsonObject();
      default:
        throw new IllegalArgumentException("Unexpected value type: " + type.name());
    }
  }

}
