package com.rapleaf.jack.queries;

public final class IndexHints {

  private IndexHints() {
  }

  public static IndexHint use(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ALL, index, indices);
  }

  public static IndexHint useForJoin(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.JOIN, index, indices);
  }

  public static IndexHint useForOrderBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ORDER_BY, index, indices);
  }

  public static IndexHint useForGroupBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.GROUP_BY, index, indices);
  }

  public static IndexHint force(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ALL, index, indices);
  }

  public static IndexHint forceForJoin(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.JOIN, index, indices);
  }

  public static IndexHint forceForOrderBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ORDER_BY, index, indices);
  }

  public static IndexHint forceForGroupBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.GROUP_BY, index, indices);
  }

  public static IndexHint ignore(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ALL, index, indices);
  }

  public static IndexHint ignoreForJoin(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.JOIN, index, indices);
  }

  public static IndexHint ignoreForOrderBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ORDER_BY, index, indices);
  }

  public static IndexHint ignoreForGroupBy(Index index, Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.GROUP_BY, index, indices);
  }

}
