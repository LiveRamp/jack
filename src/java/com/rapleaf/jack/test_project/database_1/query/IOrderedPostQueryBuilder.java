package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.Post;

public interface IOrderedPostQueryBuilder extends IOrderedQueryBuilder<Post> {
  IOrderedPostQueryBuilder select(Post._Fields... fields);

  IOrderedPostQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IOrderedPostQueryBuilder id(Long value);

  IOrderedPostQueryBuilder idIn(Set<Long> values);

  IOrderedPostQueryBuilder limit(int offset, int nResults);

  IOrderedPostQueryBuilder limit(int nResults);

  IOrderedPostQueryBuilder groupBy(Post._Fields... fields);

  IOrderedPostQueryBuilder order();

  IOrderedPostQueryBuilder order(QueryOrder queryOrder);

  IOrderedPostQueryBuilder orderById();

  IOrderedPostQueryBuilder orderById(QueryOrder queryOrder);

  IOrderedPostQueryBuilder title(String value);

  IOrderedPostQueryBuilder whereTitle(IWhereOperator<String> operator);

  IOrderedPostQueryBuilder orderByTitle();

  IOrderedPostQueryBuilder orderByTitle(QueryOrder queryOrder);

  IOrderedPostQueryBuilder postedAtMillis(Long value);

  IOrderedPostQueryBuilder wherePostedAtMillis(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByPostedAtMillis();

  IOrderedPostQueryBuilder orderByPostedAtMillis(QueryOrder queryOrder);

  IOrderedPostQueryBuilder userId(Integer value);

  IOrderedPostQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedPostQueryBuilder orderByUserId();

  IOrderedPostQueryBuilder orderByUserId(QueryOrder queryOrder);

  IOrderedPostQueryBuilder updatedAt(Long value);

  IOrderedPostQueryBuilder whereUpdatedAt(IWhereOperator<Long> operator);

  IOrderedPostQueryBuilder orderByUpdatedAt();

  IOrderedPostQueryBuilder orderByUpdatedAt(QueryOrder queryOrder);
}
