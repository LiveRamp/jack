package com.rapleaf.jack.store.json;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.rapleaf.jack.store.ValueType;

public class JsonDbTuple {

  private final List<TuplePath> paths;
  private final ValueType type;
  private final String value;

  private JsonDbTuple(List<TuplePath> paths, ValueType type, String value) {
    Preconditions.checkArgument(!paths.isEmpty(), "Value path cannot be empty: " + value);
    this.paths = paths;
    this.type = type;
    this.value = value;
  }

  public static JsonDbTuple create(String fullPath, ValueType type, String value) {
    Preconditions.checkArgument(type.getCategory() == ValueType.Category.JSON);

    List<TuplePath> paths = Lists.newLinkedList();
    for (String path : fullPath.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR))) {
      paths.add(TuplePaths.create(path));
    }

    switch (type) {
      case JSON_STRING:
        return JsonDbTuple.createString(paths, value);
      case JSON_BOOLEAN:
        return JsonDbTuple.createBoolean(paths, value);
      case JSON_NUMBER:
        return JsonDbTuple.createNumber(paths, value);
      case JSON_NULL:
        return JsonDbTuple.createNull(paths);
      case JSON_EMPTY:
        return JsonDbTuple.createEmpty(paths);
      default:
        throw new IllegalArgumentException("Unexpected json type: " + type.name());
    }
  }

  static JsonDbTuple createString(List<TuplePath> paths, String value) {
    return new JsonDbTuple(paths, ValueType.JSON_STRING, value);
  }

  static JsonDbTuple createBoolean(List<TuplePath> paths, String value) {
    return new JsonDbTuple(paths, ValueType.JSON_BOOLEAN, value);
  }

  static JsonDbTuple createNumber(List<TuplePath> paths, String value) {
    return new JsonDbTuple(paths, ValueType.JSON_NUMBER, value);
  }

  static JsonDbTuple createNull(List<TuplePath> paths) {
    return new JsonDbTuple(paths, ValueType.JSON_NULL, null);
  }

  static JsonDbTuple createEmpty(List<TuplePath> paths) {
    return new JsonDbTuple(paths, ValueType.JSON_EMPTY, null);
  }

  public String getFullPaths() {
    return Joiner.on(JsonDbConstants.PATH_SEPARATOR).join(paths);
  }

  public List<TuplePath> getPaths() {
    return paths;
  }

  public ValueType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s: %s-%s", getFullPaths(), type.name(), value);
  }

}
