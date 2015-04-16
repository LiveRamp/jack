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

public interface IPostQueryBuilder {

  PostQueryBuilder select(Post._Fields... fields);

  PostQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  PostQueryBuilder id(Long value);

  PostQueryBuilder idIn(Set<Long> values);

  PostQueryBuilder limit(int offset, int nResults);

  PostQueryBuilder limit(int nResults);

  PostQueryBuilder groupBy(Post._Fields... fields);

  IOrderedPostQueryBuilder order();

  IOrderedPostQueryBuilder order(QueryOrder queryOrder);

  IOrderedPostQueryBuilder orderById();

  IOrderedPostQueryBuilder orderById(QueryOrder queryOrder);

  PostQueryBuilder title(String value);

  PostQueryBuilder whereTitle(IWhereOperator<String> operator);

  IOrderedPostQueryBuilder orderByTitle();

  IOrderedPostQueryBuilder orderByTitle(QueryOrder queryOrder);

  PostQueryBuilder postedAtMillis(Long value);

  PostQueryBuilder wherePostedAtMillis(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByPostedAtMillis();

  IOrderedPostQueryBuilder orderByPostedAtMillis(QueryOrder queryOrder);

  PostQueryBuilder userId(Integer value);

  PostQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedPostQueryBuilder orderByUserId();

  IOrderedPostQueryBuilder orderByUserId(QueryOrder queryOrder);

  PostQueryBuilder updatedAt(Long value);

  PostQueryBuilder whereUpdatedAt(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByUpdatedAt();

  IOrderedPostQueryBuilder orderByUpdatedAt(QueryOrder queryOrder);
}
