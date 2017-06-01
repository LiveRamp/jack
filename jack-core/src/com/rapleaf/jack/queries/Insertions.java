package com.rapleaf.jack.queries;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class Insertions {

  private final List<Long> ids;

  public Insertions(List<Long> ids) {
    this.ids = ImmutableList.copyOf(ids);
  }

  public List<Long> getIds() {
    return ids;
  }

  public long getFirstId() {
    return ids.get(0);
  }

  @Override
  public String toString() {
    return Insertions.class.getSimpleName() +
        "{" +
        "ids=" + Joiner.on(",").join(ids) +
        "}";
  }

  @Override
  public int hashCode() {
    return ids.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || other instanceof Insertions
        && this.ids.equals(((Insertions)other).ids);
  }

}
