package com.rapleaf.jack.queries;

public final class IndexHints {

  private IndexHints() {
  }

  public static IndexHint use(Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ALL, indices);
  }

  public static IndexHint useForJoin(Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.JOIN, indices);
  }

  public static IndexHint useForOrderBy(Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ORDER_BY, indices);
  }

  public static IndexHint useForGroupBy(Index... indices) {
    return new IndexHint(IndexHint.Type.USE, IndexHint.Scope.GROUP_BY, indices);
  }

  public static IndexHint force(Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ALL, indices);
  }

  public static IndexHint forceForJoin(Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.JOIN, indices);
  }

  public static IndexHint forceForOrderBy(Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ORDER_BY, indices);
  }

  public static IndexHint forceForGroupBy(Index... indices) {
    return new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.GROUP_BY, indices);
  }

  public static IndexHint ignore(Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ALL, indices);
  }

  public static IndexHint ignoreForJoin(Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.JOIN, indices);
  }

  public static IndexHint ignoreForOrderBy(Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ORDER_BY, indices);
  }

  public static IndexHint ignoreForGroupBy(Index... indices) {
    return new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.GROUP_BY, indices);
  }

}
