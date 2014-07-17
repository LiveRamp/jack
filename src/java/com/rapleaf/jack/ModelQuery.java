package com.rapleaf.jack;


import java.util.Set;

public interface ModelQuery<M extends ModelWithId> {

  public Set<M> find();

}
