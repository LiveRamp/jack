package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.ISqlOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.models.Image;


public class ImageQueryBuilder extends AbstractQueryBuilder<Image> {

  public ImageQueryBuilder(IImagePersistence caller) {
    super(caller);
  }

  public ImageQueryBuilder userId(Integer value) {
    addConstraint(new QueryConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ImageQueryBuilder userId(ISqlOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(Image._Fields.user_id, operator));
    return this;
  }
}
