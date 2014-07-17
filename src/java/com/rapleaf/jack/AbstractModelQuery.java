package com.rapleaf.jack;


import java.util.Set;

public interface AbstractModelQuery<M extends ModelWithId> {

  public Set<M> find();

}
