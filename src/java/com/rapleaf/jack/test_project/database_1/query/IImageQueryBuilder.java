package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.test_project.database_1.models.Image;

public interface IImageQueryBuilder {

  IImageQueryBuilder select(Image._Fields... fields);

  IImageQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IImageQueryBuilder id(Long value);

  IImageQueryBuilder idIn(Set<Long> values);

  IImageQueryBuilder limit(int offset, int nResults);

  IImageQueryBuilder limit(int nResults);

  IImageQueryBuilder groupBy(Image._Fields... fields);

  IOrderedImageQueryBuilder order();

  IOrderedImageQueryBuilder order(QueryOrder queryOrder);

  IOrderedImageQueryBuilder orderById();

  IOrderedImageQueryBuilder orderById(QueryOrder queryOrder);

  IImageQueryBuilder userId(Integer value);

  IImageQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedImageQueryBuilder orderByUserId();

  IOrderedImageQueryBuilder orderByUserId(QueryOrder queryOrder);
}
