package com.rapleaf.jack;


import java.io.IOException;
import java.util.Set;
import java.util.List;

public interface IQueryBuilder<M extends ModelWithId> {

  public Set<M> find() throws IOException;

  public List<M> findWithOrder() throws IOException;
}
