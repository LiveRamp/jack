package com.rapleaf.jack.queries;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.rapleaf.jack.ModelWithId;

public interface IQueryBuilder<M extends ModelWithId> {

  public List<M> find() throws IOException, SQLException;
}
