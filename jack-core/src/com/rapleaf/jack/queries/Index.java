package com.rapleaf.jack.queries;

public final class Index {

  private final String name;

  private Index(String name) {
    this.name = name;
  }

  public static Index of(final String name) {
    return new Index(name);
  }

  public String getName() {
    return name;
  }

}
