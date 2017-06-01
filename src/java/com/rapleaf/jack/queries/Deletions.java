package com.rapleaf.jack.queries;

public class Deletions {

  private final int updatedRowCount;

  public Deletions(int updatedRowCount) {
    this.updatedRowCount = updatedRowCount;
  }

  public int getDeletedRowCount() {
    return updatedRowCount;
  }

}
