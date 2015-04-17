package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.Comment;

public interface IOrderedCommentQueryBuilder extends IOrderedQueryBuilder<Comment> {
  IOrderedCommentQueryBuilder select(Comment._Fields... fields);

  IOrderedCommentQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IOrderedCommentQueryBuilder id(Long value);

  IOrderedCommentQueryBuilder idIn(Set<Long> values);

  IOrderedCommentQueryBuilder limit(int offset, int nResults);

  IOrderedCommentQueryBuilder limit(int nResults);

  IOrderedCommentQueryBuilder groupBy(Comment._Fields... fields);

  IOrderedCommentQueryBuilder order();

  IOrderedCommentQueryBuilder order(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder orderById();

  IOrderedCommentQueryBuilder orderById(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder content(String value);

  IOrderedCommentQueryBuilder whereContent(IWhereOperator<String> operator);

  IOrderedCommentQueryBuilder orderByContent();

  IOrderedCommentQueryBuilder orderByContent(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder commenterId(Integer value);

  IOrderedCommentQueryBuilder whereCommenterId(IWhereOperator<Integer> operator);

  IOrderedCommentQueryBuilder orderByCommenterId();

  IOrderedCommentQueryBuilder orderByCommenterId(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder commentedOnId(Long value);

  IOrderedCommentQueryBuilder whereCommentedOnId(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCommentedOnId();

  IOrderedCommentQueryBuilder orderByCommentedOnId(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder createdAt(Long value);

  IOrderedCommentQueryBuilder whereCreatedAt(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCreatedAt();

  IOrderedCommentQueryBuilder orderByCreatedAt(QueryOrder queryOrder);
}
