package com.rapleaf.java_active_record;

import java.io.Serializable;

public abstract class ModelWithId<ID extends Number> implements Serializable {
  private final ID id;

  protected ModelWithId(ID id) {
    this.id = id;
  }

  public ID getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    long result = 1;
    result = prime * result + id.longValue();
    return (int) result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ModelWithId other = (ModelWithId) obj;
    if (id != other.id)
      return false;
    return true;
  }
}