package com.rapleaf.jack.queries;

public final class IndexHints {

  private IndexHints() {
  }

  public static IndexHint use(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ALL, index, otherIndices);
  }

  public static IndexHint useForJoin(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.JOIN, index, otherIndices);
  }

  public static IndexHint useForOrderBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ORDER_BY, index, otherIndices);
  }

  public static IndexHint useForGroupBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.GROUP_BY, index, otherIndices);
  }

  public static IndexHint force(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ALL, index, otherIndices);
  }

  public static IndexHint forceForJoin(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.JOIN, index, otherIndices);
  }

  public static IndexHint forceForOrderBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ORDER_BY, index, otherIndices);
  }

  public static IndexHint forceForGroupBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.GROUP_BY, index, otherIndices);
  }

  public static IndexHint ignore(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ALL, index, otherIndices);
  }

  public static IndexHint ignoreForJoin(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.JOIN, index, otherIndices);
  }

  public static IndexHint ignoreForOrderBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ORDER_BY, index, otherIndices);
  }

  public static IndexHint ignoreForGroupBy(Index index, Index... otherIndices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.GROUP_BY, index, otherIndices);
  }

}
