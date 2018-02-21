package com.rapleaf.jack.queries;

public class Updates {

  private final int updatedRowCount;

  public Updates(int updatedRowCount) {
    this.updatedRowCount = updatedRowCount;
  }

  public int getUpdatedRowCount() {
    return updatedRowCount;
  }

  @Override
  public String toString() {
    return Updates.class.getSimpleName() +
        "{" +
        "updateRowCount=" + updatedRowCount +
        "}";
  }

  @Override
  public int hashCode() {
    return updatedRowCount;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || other instanceof Updates
        && this.updatedRowCount == ((Updates)other).updatedRowCount;
  }

}
