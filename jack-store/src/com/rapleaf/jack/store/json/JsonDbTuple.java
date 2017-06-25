package com.rapleaf.jack.store.json;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import com.rapleaf.jack.store.ValueType;

public class JsonDbTuple {

  private final List<TuplePath> paths;
  private final ValueType type;
  /**
   * When value is present, it represents json value (string, number, boolean)
   * When value is "null", it represents json null value
   * When value is null, it represents empty json value ("{}" or "[]")
   */
  private final String value;

  private JsonDbTuple(List<TuplePath> paths, ValueType type, String value) {
    Preconditions.checkArgument(!paths.isEmpty(), "Value path cannot be empty: " + value);
    this.paths = paths;
    this.type = type;
    this.value = value;
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
