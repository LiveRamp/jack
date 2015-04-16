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
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;

public class PostQueryBuilder extends AbstractQueryBuilder<Post> implements IPostQueryBuilder {

  public PostQueryBuilder(IPostPersistence caller) {
    super(caller);
  }

  public IPostQueryBuilder select(Post._Fields... fields) {
    for (Post._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public IPostQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public IPostQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public IPostQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public IPostQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public IPostQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public IPostQueryBuilder groupBy(Post._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public IOrderedPostQueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderById(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }

  public IPostQueryBuilder title(String value) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, JackMatchers.equalTo(value)));
    return this;
  }

  public IPostQueryBuilder whereTitle(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, operator));
    return this;
  }
  
  public IOrderedPostQueryBuilder orderByTitle() {
    this.addOrder(new OrderCriterion(Post._Fields.title, QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderByTitle(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.title, queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }

  public IPostQueryBuilder postedAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public IPostQueryBuilder wherePostedAtMillis(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, operator));
    return this;
  }
  
  public IOrderedPostQueryBuilder orderByPostedAtMillis() {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderByPostedAtMillis(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }

  public IPostQueryBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public IPostQueryBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, operator));
    return this;
  }
  
  public IOrderedPostQueryBuilder orderByUserId() {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderByUserId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }

  public IPostQueryBuilder updatedAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, JackMatchers.equalTo(value)));
    return this;
  }

  public IPostQueryBuilder whereUpdatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, operator));
    return this;
  }
  
  public IOrderedPostQueryBuilder orderByUpdatedAt() {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, QueryOrder.ASC));
    return (IOrderedPostQueryBuilder)this;
  }
  
  public IOrderedPostQueryBuilder orderByUpdatedAt(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, queryOrder));
    return (IOrderedPostQueryBuilder)this;
  }
}
