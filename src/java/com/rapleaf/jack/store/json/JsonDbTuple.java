package com.rapleaf.jack.store.json;

import java.util.Optional;

public class JsonDbTuple {

  private final String path;
  private final String key;
  private final String value;
  private final Optional<Integer> listIndex;
  private final Optional<Integer> listSize;

  public JsonDbTuple(String path, String key, String value, Integer listIndex, Integer listSize) {
    this.path = path;
    this.key = key;
    this.value = value;
    this.listIndex = Optional.of(listIndex);
    this.listSize = Optional.of(listSize);
  }

  public JsonDbTuple(String path, String key, String value) {
    this.path = path;
    this.key = key;
    this.value = value;
    this.listIndex = Optional.empty();
    this.listSize = Optional.empty();
  }

  public String getPath() {
    return path;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public Optional<Integer> getListIndex() {
    return listIndex;
  }

  public Optional<Integer> getListSize() {
    return listSize;
  }

  @Override
  public String toString() {
    return "JsonDbTuple{" +
        "path='" + path + '\'' +
        ", key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", listIndex=" + listIndex +
        ", listSize=" + listSize +
        '}';
  }
}
