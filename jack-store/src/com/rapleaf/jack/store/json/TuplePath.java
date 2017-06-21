package com.rapleaf.jack.store.json;

import java.util.Optional;

public interface TuplePath {

  Optional<String> getName();

  boolean isArray();

  Optional<Integer> getListIndex();

  Optional<Integer> getListSize();

  @Override
  String toString();

}
