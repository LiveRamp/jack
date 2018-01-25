package com.rapleaf.jack.queries;

import java.util.List;

public interface JoinableExecution {

  abstract void addParameters(List parameters);

  abstract void addJoinCondition(JoinCondition joinCondition);

}
