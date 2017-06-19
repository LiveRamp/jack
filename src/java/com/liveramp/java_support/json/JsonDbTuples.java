package com.liveramp.java_support.json;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class JsonDbTuples implements Iterable<JsonDbTuple> {

  private final List<JsonDbTuple> tuples;

  public JsonDbTuples(List<JsonDbTuple> tuples) {
    this.tuples = tuples;
  }

  public int size() {
    return tuples.size();
  }

  public List<JsonDbTuple> getTupes() {
    return tuples;
  }

  @NotNull
  @Override
  public Iterator<JsonDbTuple> iterator() {
    return tuples.iterator();
  }

}
