package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import com.rapleaf.jack.ModelWithId;

public interface IUnorderedQueryBuilder<M extends ModelWithId> {

  Set<M> find() throws IOException, SQLException;
}
