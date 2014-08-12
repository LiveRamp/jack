package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.models.Image;


public class ImageQueryBuilder extends AbstractQueryBuilder<Image> {

  public ImageQueryBuilder(IImagePersistence caller) {
    super(caller);
  }

  public ImageQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public ImageQueryBuilder id(Set<Long> values) {
    addIds(values);
    return this;
  }

  public ImageQueryBuilder userId(Integer value) {
    addConstraint(new QueryConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ImageQueryBuilder userId(IQueryOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(Image._Fields.user_id, operator));
    return this;
  }
}
