package com.rapleaf.jack.store;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class JsScopes implements Iterable<JsScope> {

  private final List<JsScope> jsScopes;

  private JsScopes(List<JsScope> jsScopes) {
    this.jsScopes = ImmutableList.copyOf(jsScopes);
  }

  public static JsScopes of(List<JsScope> jsScopes) {
    if (jsScopes == null) {
      return JsConstants.EMPTY_SCOPES;
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

  public JsScope getOnly() {
    Preconditions.checkState(jsScopes.size() == 1, "There are more than one (%s) scopes", jsScopes.size());
    return jsScopes.get(0);
  }

  public JsScope getFirst() {
    Preconditions.checkState(jsScopes.size() >= 1, "No scope exists");
    return jsScopes.get(0);
  }

  public JsScope get(int index) {
    Preconditions.checkState(jsScopes.size() > index, "There are only %s scope(s), index %s is out of bound", jsScopes.size(), index);
    return jsScopes.get(index);
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
