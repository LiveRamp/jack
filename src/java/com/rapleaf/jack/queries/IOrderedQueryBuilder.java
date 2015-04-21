package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.rapleaf.jack.ModelWithId;

public interface IOrderedQueryBuilder<M extends ModelWithId> {

  List<M> findWithOrder() throws IOException, SQLException;
}
