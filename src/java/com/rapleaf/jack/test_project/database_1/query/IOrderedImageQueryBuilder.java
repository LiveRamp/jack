package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.Image;

public interface IOrderedImageQueryBuilder extends IOrderedQueryBuilder<Image> {
  IOrderedImageQueryBuilder select(Image._Fields... fields);

  IOrderedImageQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IOrderedImageQueryBuilder id(Long value);

  IOrderedImageQueryBuilder idIn(Set<Long> values);

  IOrderedImageQueryBuilder limit(int offset, int nResults);

  IOrderedImageQueryBuilder limit(int nResults);

  IOrderedImageQueryBuilder groupBy(Image._Fields... fields);

  IOrderedImageQueryBuilder order();

  IOrderedImageQueryBuilder order(QueryOrder queryOrder);

  IOrderedImageQueryBuilder orderById();

  IOrderedImageQueryBuilder orderById(QueryOrder queryOrder);

  IOrderedImageQueryBuilder userId(Integer value);

  IOrderedImageQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedImageQueryBuilder orderByUserId();

  IOrderedImageQueryBuilder orderByUserId(QueryOrder queryOrder);
}
