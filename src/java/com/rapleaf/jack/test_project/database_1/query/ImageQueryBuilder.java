package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.AbstractQueryBuilder;
import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.OrderCriterion;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.models.Image;

public class ImageQueryBuilder extends AbstractQueryBuilder<Image> implements IImageQueryBuilder {

  public ImageQueryBuilder(IImagePersistence caller) {
    super(caller);
  }

  public IImageQueryBuilder select(Image._Fields... fields) {
    for (Image._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public IImageQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public IImageQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public IImageQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public IImageQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public IImageQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public IImageQueryBuilder groupBy(Image._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public IOrderedImageQueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedImageQueryBuilder)this;
  }
  
  public IOrderedImageQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedImageQueryBuilder)this;
  }
  
  public IOrderedImageQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedImageQueryBuilder)this;
  }
  
  public IOrderedImageQueryBuilder orderById(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedImageQueryBuilder)this;
  }

  public IImageQueryBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public IImageQueryBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, operator));
    return this;
  }
  
  public IOrderedImageQueryBuilder orderByUserId() {
    this.addOrder(new OrderCriterion(Image._Fields.user_id, QueryOrder.ASC));
    return (IOrderedImageQueryBuilder)this;
  }
  
  public IOrderedImageQueryBuilder orderByUserId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Image._Fields.user_id, queryOrder));
    return (IOrderedImageQueryBuilder)this;
  }
}
