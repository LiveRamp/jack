package com.liveramp.java_support.json;

import java.util.List;

import com.google.common.base.Joiner;

public class JsonDbTuple {

  private final List<TuplePath> paths;
  private final String value;

  JsonDbTuple(List<TuplePath> paths, String value) {
    this.paths = paths;
    this.value = value;
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
