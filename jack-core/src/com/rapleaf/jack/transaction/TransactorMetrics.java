package com.rapleaf.jack.transaction;

import java.util.LinkedList;

public interface TransactorMetrics {

  LinkedList<TransactorMetricElement> getLongestQueries();

  String getSummary();

}
