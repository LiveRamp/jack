package com.rapleaf.jack.transaction;

public interface DbPoolStatus {

  int getNumActive();

  int getNumIdle();

  int getNumWaiters();
}
