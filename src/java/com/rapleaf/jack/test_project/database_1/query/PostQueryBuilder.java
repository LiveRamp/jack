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
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;


public class PostQueryBuilder extends AbstractQueryBuilder<Post> {

  public PostQueryBuilder(IPostPersistence caller) {
    super(caller);
  }

  public PostQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public PostQueryBuilder id(Set<Long> values) {
    addIds(values);
    return this;
  }

  public PostQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public PostQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public PostQueryBuilder select(Post._Fields... fields) {
    for (Post._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public PostQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public PostQueryBuilder groupBy(Post._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public PostQueryBuilder order() {
    this.addOrder(new OrderCriterion(null, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(null, queryOrder));
    return this;
  }
  
  public PostQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(null, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder orderById(QueryOrder queryOrder) {    
    this.addOrder(new OrderCriterion(null, queryOrder));
    return this;
  }

  public PostQueryBuilder title(String value) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereTitle(IQueryOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, operator));
    return this;
  }
  
  public PostQueryBuilder orderByTitle() {
    this.addOrder(new OrderCriterion(Post._Fields.title, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder orderByTitle(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.title, queryOrder));
    return this;
  }

  public PostQueryBuilder postedAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder wherePostedAtMillis(IQueryOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, operator));
    return this;
  }
  
  public PostQueryBuilder orderByPostedAtMillis() {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder orderByPostedAtMillis(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, queryOrder));
    return this;
  }

  public PostQueryBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereUserId(IQueryOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, operator));
    return this;
  }
  
  public PostQueryBuilder orderByUserId() {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder orderByUserId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, queryOrder));
    return this;
  }

  public PostQueryBuilder updatedAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereUpdatedAt(IQueryOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, operator));
    return this;
  }
  
  public PostQueryBuilder orderByUpdatedAt() {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, QueryOrder.ASC));
    return this;
  }
  
  public PostQueryBuilder orderByUpdatedAt(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, queryOrder));
    return this;
  }
}
