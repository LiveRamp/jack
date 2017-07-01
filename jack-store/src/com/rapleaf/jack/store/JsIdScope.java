package com.rapleaf.jack.store;

import org.apache.commons.lang.NotImplementedException;

public class JsIdScope extends JsScope {

  public JsIdScope(Long scopeId) {
    super(null, scopeId, null);
  }

  @Override
  public Long getParentScopeId() {
    throw new NotImplementedException();
  }

  public String getScopeName() {
    throw new NotImplementedException();
  }

  public boolean isRootScope() {
    throw new NotImplementedException();
  }

}
