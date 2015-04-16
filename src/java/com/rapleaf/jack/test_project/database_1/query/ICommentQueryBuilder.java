package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.test_project.database_1.models.Comment;

public interface ICommentQueryBuilder {

  CommentQueryBuilder select(Comment._Fields... fields);

  CommentQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  CommentQueryBuilder id(Long value);

  CommentQueryBuilder idIn(Set<Long> values);

  CommentQueryBuilder limit(int offset, int nResults);

  CommentQueryBuilder limit(int nResults);

  CommentQueryBuilder groupBy(Comment._Fields... fields);

  IOrderedCommentQueryBuilder order();

  IOrderedCommentQueryBuilder order(QueryOrder queryOrder);

  IOrderedCommentQueryBuilder orderById();

  IOrderedCommentQueryBuilder orderById(QueryOrder queryOrder);

  CommentQueryBuilder content(String value);

  CommentQueryBuilder whereContent(IWhereOperator<String> operator);

  IOrderedCommentQueryBuilder orderByContent();

  IOrderedCommentQueryBuilder orderByContent(QueryOrder queryOrder);

  CommentQueryBuilder commenterId(Integer value);

  CommentQueryBuilder whereCommenterId(IWhereOperator<Integer> operator);

  IOrderedCommentQueryBuilder orderByCommenterId();

  IOrderedCommentQueryBuilder orderByCommenterId(QueryOrder queryOrder);

  CommentQueryBuilder commentedOnId(Long value);

  CommentQueryBuilder whereCommentedOnId(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCommentedOnId();

  IOrderedCommentQueryBuilder orderByCommentedOnId(QueryOrder queryOrder);

  CommentQueryBuilder createdAt(Long value);

  CommentQueryBuilder whereCreatedAt(IWhereOperator<Long> operator);

  IOrderedCommentQueryBuilder orderByCreatedAt();

  IOrderedCommentQueryBuilder orderByCreatedAt(QueryOrder queryOrder);
}
