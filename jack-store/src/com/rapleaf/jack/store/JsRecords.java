package com.rapleaf.jack.store;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Joiner;

public class JsRecords implements Iterable<JsRecord> {

  private final List<JsRecord> jsRecords;

  public JsRecords(List<JsRecord> jsRecords) {
    this.jsRecords = jsRecords;
  }

  @Override
  public Iterator<JsRecord> iterator() {
    return jsRecords.iterator();
  }

  public boolean isEmpty() {
    return jsRecords.isEmpty();
  }

  public int size() {
    return jsRecords.size();
  }

  public Stream<JsRecord> stream() {
    return jsRecords.stream();
  }

  @Override
  public int hashCode() {
    return jsRecords.hashCode();
  }

  @Override
  public String toString() {
    return JsRecords.class.getSimpleName() +
        "{" +
        "records=[" + Joiner.on(",").join(jsRecords) + "]" +
        "}";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsRecords)) {
      return false;
    }

    JsRecords that = (JsRecords)other;
    return this.jsRecords.equals(that.jsRecords);
  }
}
