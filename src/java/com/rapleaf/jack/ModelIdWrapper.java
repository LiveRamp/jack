package com.rapleaf.jack;

import java.io.Serializable;

public interface ModelIdWrapper<T extends ModelIdWrapper> extends Serializable, Comparable<T>{

  public Long getId();
}
