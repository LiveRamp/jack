package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.AbstractDeleteBuilder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.models.Image;


public class ImageDeleteBuilder extends AbstractDeleteBuilder<Image> {

  public ImageDeleteBuilder(IImagePersistence caller) {
    super(caller);
  }

  public ImageDeleteBuilder id(Long value) {
    addId(value);
    return this;
  }

  public ImageDeleteBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public ImageDeleteBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ImageDeleteBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, operator));
    return this;
  }
}
