package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;


public class ImageQueryBuilder extends AbstractQueryBuilder<Image> {

  public ImageQueryBuilder (IImagePersistence caller) {
    super(caller);
  }

  public ImageQueryBuilder userId(Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public ImageQueryBuilder userId(QueryConstraint<Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }
}
