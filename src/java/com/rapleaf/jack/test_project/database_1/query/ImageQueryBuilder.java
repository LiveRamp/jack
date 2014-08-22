package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.FieldSelector;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.WhereConstraint;
import com.rapleaf.jack.QueryOrder;
import com.rapleaf.jack.OrderCriterion;
import com.rapleaf.jack.LimitCriterion;
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

  public ImageQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public ImageQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public ImageQueryBuilder select(Image._Fields... fields) {
    for (Image._Fields field : fields){
      addSelectedFields(new FieldSelector(fields));
    }
    return this;
  }

  public ImageQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addAggregatedFields(aggregatedFields);
    return this;
  }

  public ImageQueryBuilder groupBy(Image._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public ImageQueryBuilder order() {
    this.addOrder(new OrderCriterion(null, QueryOrder.ASC));
    return this;
  }
  
  public ImageQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(null, queryOrder));
    return this;
  }
  
  public ImageQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(null, QueryOrder.ASC));
    return this;
  }
  
  public ImageQueryBuilder orderById(QueryOrder queryOrder) {    
    this.addOrder(new OrderCriterion(null, queryOrder));
    return this;
  }

  public ImageQueryBuilder userId(Integer value) {
    if(value == null) {
      addConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, JackMatchers.<Integer>isNull()));
    }
    else {
      addConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public ImageQueryBuilder userId(IQueryOperator<Integer> operator) {
    addConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, operator));
    return this;
  }
  
  public ImageQueryBuilder orderByUserId() {
    this.addOrder(new OrderCriterion(Image._Fields.user_id, QueryOrder.ASC));
    return this;
  }
  
  public ImageQueryBuilder orderByUserId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Image._Fields.user_id, queryOrder));
    return this;
  }
}
