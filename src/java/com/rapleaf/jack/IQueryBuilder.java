package com.rapleaf.jack;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.List;

public interface IQueryBuilder<M extends ModelWithId> {

  public Set<M> find() throws IOException, SQLException;

  public List<M> findWithOrder() throws IOException, SQLException;
}
