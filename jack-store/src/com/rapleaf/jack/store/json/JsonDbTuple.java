package com.rapleaf.jack.store.json;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import com.rapleaf.jack.store.JsConstants;

public class JsonDbTuple {

  private final List<TuplePath> paths;
  /**
   * When value is present, it represents json value (string, number, boolean)
   * When value is "null", it represents json null value
   * When value is null, it represents empty json value ("{}" or "[]")
   */
  private final String value;

  private JsonDbTuple(List<TuplePath> paths, String value) {
    Preconditions.checkArgument(!paths.isEmpty(), "Value path cannot be empty: " + value);
    this.paths = paths;
    this.value = value;
  }

  static JsonDbTuple create(List<TuplePath> paths, String value) {
    return new JsonDbTuple(paths, value);
  }

  static JsonDbTuple createNull(List<TuplePath> paths) {
    return new JsonDbTuple(paths, JsonDbConstants.NULL_VALUE);
  }

  static JsonDbTuple createEmptyObject(List<TuplePath> paths) {
    return new JsonDbTuple(paths, null);
  }

  List<TuplePath> getPaths() {
    return paths;
  }

  String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", Joiner.on(JsonDbConstants.PATH_SEPARATOR).join(paths), value);
  }

}
