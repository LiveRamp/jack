package com.liveramp.java_support.json;

import java.util.Optional;

public class ElementPath implements TuplePath {

  private final String name;

  public ElementPath(String name) {
    this.name = name;
  }

  @Override
  public Optional<String> getName() {
    return Optional.of(name);
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public Optional<Integer> getListIndex() {
    return Optional.empty();
  }

  @Override
  public Optional<Integer> getListSize() {
    return Optional.empty();
  }

  @Override
  public String toString() {
    return name;
  }

}
