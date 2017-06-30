package com.rapleaf.jack.store;

import java.util.Objects;

public class JsScope {

  private final Long parentScopeId;
  private final Long scopeId;
  private final String scopeName;

  public JsScope(Long parentScopeId, Long scopeId, String scopeName) {
    this.parentScopeId = parentScopeId;
    this.scopeId = scopeId;
    this.scopeName = scopeName;
  }

  public static JsScope root() {
    return JsConstants.ROOT_SCOPE;
  }

  public Long getParentScopeId() {
    return parentScopeId;
  }

  public Long getScopeId() {
    return scopeId;
  }

  public String getScopeName() {
    return scopeName;
  }

  public boolean isRootScope() {
    return scopeId == null;
  }

  @Override
  public int hashCode() {
    return String.valueOf(scopeId).hashCode() + scopeName.hashCode();
  }

  @Override
  public String toString() {
    return JsScope.class.getSimpleName() +
        "{" +
        "scopeId=" + scopeId +
        ",scopeName=" + scopeName +
        "}";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsScope)) {
      return false;
    }

    JsScope that = (JsScope)other;
    return Objects.equals(this.scopeId, that.scopeId) && Objects.equals(this.scopeName, that.scopeName);
  }

}
