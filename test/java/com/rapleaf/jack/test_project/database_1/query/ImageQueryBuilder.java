package com.rapleaf.jack.test_project.database_1.query;

import java.util.Collection;

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


public class ImageQueryBuilder extends AbstractQueryBuilder<Image> {

  public ImageQueryBuilder(IImagePersistence caller) {
    super(caller);
  }

  public ImageQueryBuilder select(Image._Fields... fields) {
    for (Image._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public ImageQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public ImageQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public ImageQueryBuilder idIn(Collection<Long> values) {
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

  public ImageQueryBuilder groupBy(Image._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public ImageQueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }
  
  public ImageQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }
  
  public ImageQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }
  
  public ImageQueryBuilder orderById(QueryOrder queryOrder) {    
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }

  public ImageQueryBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ImageQueryBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Image._Fields.user_id, operator));
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
