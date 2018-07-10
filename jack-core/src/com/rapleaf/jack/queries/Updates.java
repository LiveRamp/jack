package com.rapleaf.jack.queries;

public class Updates {

  private final int matchedRowCount;

  public Updates(int matchedRowCount) {
    this.matchedRowCount = matchedRowCount;
  }

  /**
   * @return the number of rows matched by the update statement. Matched rows may not have changed.
   * See comments in {@link UpdateFetcher#getUpdateResults}.
   */
  public int getMatchedRowCount() {
    return matchedRowCount;
  }

  @Override
  public String toString() {
    return Updates.class.getSimpleName() +
        "{" +
        "matchedRowCount=" + matchedRowCount +
        "}";
  }

  @Override
  public int hashCode() {
    return matchedRowCount;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || other instanceof Updates
        && this.matchedRowCount == ((Updates)other).matchedRowCount;
  }

}
