package com.rapleaf.jack;


import java.util.Set;

public interface AbstractModelQuery<T> {

  public Set<T> find();

}
