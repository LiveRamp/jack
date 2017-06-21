package com.liveramp.java_support.json;

import java.util.Optional;

public interface TuplePath {

  Optional<String> getName();

  boolean isArray();

  Optional<Integer> getListIndex();

  Optional<Integer> getListSize();

  @Override
  String toString();

}
