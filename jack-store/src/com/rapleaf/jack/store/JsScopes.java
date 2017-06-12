package com.rapleaf.jack.store;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class JsScopes implements Iterable<JsScope> {

  private static final JsScopes EMPTY_SCOPES = new JsScopes(Collections.emptyList());

  private final List<JsScope> jsScopes;

  private JsScopes(List<JsScope> jsScopes) {
    this.jsScopes = ImmutableList.copyOf(jsScopes);
  }

  public static JsScopes of(List<JsScope> jsScopes) {
    if (jsScopes == null || jsScopes.isEmpty()) {
      return EMPTY_SCOPES;
    }
    return new JsScopes(jsScopes);
  }

  @Override
  public Iterator<JsScope> iterator() {
    return jsScopes.iterator();
  }

  public List<JsScope> getScopes() {
    return jsScopes;
  }

  public List<Long> getScopeIds() {
    return stream().map(JsScope::getScopeId).collect(Collectors.toList());
  }

  public List<String> getScopeNames() {
    return stream().map(JsScope::getScopeName).collect(Collectors.toList());
  }

  public boolean isEmpty() {
    return jsScopes.isEmpty();
  }

  public int size() {
    return jsScopes.size();
  }

  public Stream<JsScope> stream() {
    return jsScopes.stream();
  }

  @Override
  public int hashCode() {
    return jsScopes.hashCode();
  }

  @Override
  public String toString() {
    return JsScopes.class.getSimpleName() +
        "{" +
        "scopes=[" + Joiner.on(",").join(jsScopes) + "]" +
        "}";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsScopes)) {
      return false;
    }

    JsScopes that = (JsScopes)other;
    return this.jsScopes.equals(that.jsScopes);
  }
}
