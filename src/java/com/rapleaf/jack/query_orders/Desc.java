package com.rapleaf.jack.query_orders;

import com.rapleaf.jack.QueryOrder;

public class Desc extends QueryOrder {
  private static Desc instance;
  
  Desc(){
    super("DESC");
  }
  
  public QueryOrder getInstance() {
    // The return statement is duplicated to allow a fast path to retrieve
    //   the existing instance, which is likely to be called more frequently
    //   than the alternative.
    if (this.instance != null) {
      return instance;
    }
    this.instance = new Desc();
    return instance;
  }
}
