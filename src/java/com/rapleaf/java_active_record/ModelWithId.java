package com.rapleaf.java_active_record;

import java.io.Serializable;

public abstract class ModelWithId implements Serializable {
  private final int id;

  protected ModelWithId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    long result = 1;
    result = prime * result + id;
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