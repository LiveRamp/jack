package com.liveramp.java_support.json;

import java.util.Optional;
import java.util.regex.Pattern;

public class JsonDbTuple {

  private final String path;
  private final String value;

  private final Optional<String> key;
  private final boolean isList;
  private final Optional<Integer> listIndex;
  private final Optional<Integer> listSize;

  JsonDbTuple(String path, String value) {
    this.path = path;
    this.value = value;

    String[] paths = path.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR));
    int pathSize = paths.length;

    if (paths.length >= 1) {
      String listPath = paths[pathSize - 1];
      String[] listPaths = listPath.split(Pattern.quote(JsonDbConstants.LIST_PATH_SEPARATOR));
      if (listPaths.length == 1) {
        this.key = Optional.of(listPath);
        this.isList = false;
        this.listIndex = Optional.empty();
        this.listSize = Optional.empty();
      } else if (listPaths.length == 3) {
        if (listPaths[0].equals(JsonDbConstants.KEYLESS_ARRAY_NAME)) {
          this.key = Optional.empty();
        } else {
          this.key = Optional.of(listPaths[0]);
        }
        this.isList = true;
        this.listIndex = Optional.of(Integer.valueOf(listPaths[1]));
        this.listSize = Optional.of(Integer.valueOf(listPaths[2]));
      } else {
        throw new IllegalArgumentException("Invalid path: " + path);
      }
    } else {
      this.key = Optional.of(path);
      this.isList = false;
      this.listIndex = Optional.empty();
      this.listSize = Optional.empty();
    }
  }

  String getPath() {
    return path;
  }

  String getValue() {
    return value;
  }

  Optional<String> getKey() {
    return key;
  }

  boolean isArray() {
    return isList;
  }

  Optional<Integer> getListIndex() {
    return listIndex;
  }

  Optional<Integer> getListSize() {
    return listSize;
  }

  @Override
  public String toString() {
    return "JsonDbTuple{" +
        "path='" + path + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

}
