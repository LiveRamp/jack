package com.liveramp.java_support.json;

import java.util.Optional;

import com.google.common.base.Preconditions;

public class JsonDbTuple {

  private final String path;
  private final String value;

  private final String key;
  private final Optional<Integer> listIndex;
  private final Optional<Integer> listSize;

  JsonDbTuple(String path, String value) {
    this.path = path;
    this.value = value;

    String[] paths = path.split(JsonDbConstants.PATH_SEPARATOR);
    int pathSize = paths.length;

    if (pathSize >= 1) {
      this.key = paths[pathSize - 1];
    } else {
      this.key = "";
    }

    if (paths.length >= 2) {
      String listPath = paths[pathSize - 2];
      String[] listPaths = listPath.split(JsonDbConstants.LIST_PATH_SEPARATOR);
      Preconditions.checkArgument(listPaths.length == 3);
      this.listIndex = Optional.of(Integer.valueOf(listPaths[1]));
      this.listSize = Optional.of(Integer.valueOf(listPaths[2]));
    } else {
      this.listIndex = Optional.empty();
      this.listSize = Optional.empty();
    }
  }

  public String getPath() {
    return path;
  }

  public String getValue() {
    return value;
  }

  String getKey() {
    return key;
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
