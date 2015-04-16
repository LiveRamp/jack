package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.test_project.database_1.models.Comment;

public interface ICommentQueryBuilder {

  ICommentQueryBuilder select(Comment._Fields... fields);

  ICommentQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  ICommentQueryBuilder id(Long value);

  ICommentQueryBuilder idIn(Set<Long> values);

  ICommentQueryBuilder limit(int offset, int nResults);

  ICommentQueryBuilder limit(int nResults);

  ICommentQueryBuilder groupBy(Comment._Fields... fields);

  IOrderedCommentQueryBuilder order();

  IOrderedCommentQueryBuilder order(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder orderById();

  IOrderedCommentQueryBuilder orderById(QueryOrder queryOrder);

  ICommentQueryBuilder content(String value);

  ICommentQueryBuilder whereContent(IWhereOperator<String> operator);

  IOrderedCommentQueryBuilder orderByContent();

  IOrderedCommentQueryBuilder orderByContent(QueryOrder queryOrder);

  ICommentQueryBuilder commenterId(Integer value);

  ICommentQueryBuilder whereCommenterId(IWhereOperator<Integer> operator);

  IOrderedCommentQueryBuilder orderByCommenterId();

  IOrderedCommentQueryBuilder orderByCommenterId(QueryOrder queryOrder);

  ICommentQueryBuilder commentedOnId(Long value);

  ICommentQueryBuilder whereCommentedOnId(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCommentedOnId();

  IOrderedCommentQueryBuilder orderByCommentedOnId(QueryOrder queryOrder);

  ICommentQueryBuilder createdAt(Long value);

  ICommentQueryBuilder whereCreatedAt(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCreatedAt();

  IOrderedCommentQueryBuilder orderByCreatedAt(QueryOrder queryOrder);
}
