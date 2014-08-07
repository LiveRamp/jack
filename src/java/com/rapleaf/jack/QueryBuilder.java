package com.rapleaf.jack;


import java.io.IOException;
import java.util.Set;

public interface QueryBuilder<M extends ModelWithId> {

  public Set<M> find() throws IOException;

}
