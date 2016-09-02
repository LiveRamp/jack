package com.rapleaf.jack.tracking;

import java.io.Serializable;

public interface PostQueryAction extends Serializable {
  void perform(QueryStatistics statistics);
}
