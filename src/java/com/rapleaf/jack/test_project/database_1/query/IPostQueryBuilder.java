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

  IPostQueryBuilder select(Post._Fields... fields);

  IPostQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IPostQueryBuilder id(Long value);

  IPostQueryBuilder idIn(Set<Long> values);

  IPostQueryBuilder limit(int offset, int nResults);

  IPostQueryBuilder limit(int nResults);

  IPostQueryBuilder groupBy(Post._Fields... fields);

  IOrderedPostQueryBuilder order();

  IOrderedPostQueryBuilder order(QueryOrder queryOrder);

  IOrderedPostQueryBuilder orderById();

  IOrderedPostQueryBuilder orderById(QueryOrder queryOrder);

  IPostQueryBuilder title(String value);

  IPostQueryBuilder whereTitle(IWhereOperator<String> operator);

  IOrderedPostQueryBuilder orderByTitle();

  IOrderedPostQueryBuilder orderByTitle(QueryOrder queryOrder);

  IPostQueryBuilder postedAtMillis(Long value);

  IPostQueryBuilder wherePostedAtMillis(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByPostedAtMillis();

  IOrderedPostQueryBuilder orderByPostedAtMillis(QueryOrder queryOrder);

  IPostQueryBuilder userId(Integer value);

  IPostQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedPostQueryBuilder orderByUserId();

  IOrderedPostQueryBuilder orderByUserId(QueryOrder queryOrder);

  IPostQueryBuilder updatedAt(Long value);

  IPostQueryBuilder whereUpdatedAt(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByUpdatedAt();

  IOrderedPostQueryBuilder orderByUpdatedAt(QueryOrder queryOrder);
}
