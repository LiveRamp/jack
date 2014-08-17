package com.rapleaf.jack.query_orders;

import com.rapleaf.jack.QueryOrder;

public class Asc extends QueryOrder {
  private static Asc instance;
  
  Asc(){
    super("ASC");
  }
  
  public QueryOrder getInstance() {
    // The return statement is duplicated to allow a fast path to retrieve
    //   the existing instance, which is likely to be called more frequently
    //   than the alternative.
    if (this.instance != null) {
      return instance;
    }
    this.instance = new Asc();
    return instance;
  }
}
