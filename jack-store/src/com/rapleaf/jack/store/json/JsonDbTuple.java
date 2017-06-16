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

  @Override
  public int hashCode() {
    int hashCode = path.hashCode();
    hashCode += 19 * key.hashCode();
    hashCode += 19 * value.hashCode();
    hashCode += 19 * listIndex.hashCode();
    hashCode += 19 * listSize.hashCode();
    return hashCode;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsonDbTuple)) {
      return false;
    }

    JsonDbTuple that = (JsonDbTuple)other;
    return this.path.equals(that.path) &&
        this.key.equals(that.key) &&
        this.value.equals(that.value) &&
        this.listIndex.equals(that.listIndex) &&
        this.listSize.equals(that.listSize);
  }
}
