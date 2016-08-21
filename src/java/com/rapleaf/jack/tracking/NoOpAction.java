package com.rapleaf.jack.tracking;

public class NoOpAction implements PostQueryAction {
  @Override
  public void perform(QueryStatistics statistics) { }
}
