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
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.models.Image;

public interface IImageQueryBuilder {

  ImageQueryBuilder select(Image._Fields... fields);

  ImageQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  ImageQueryBuilder id(Long value);

  ImageQueryBuilder idIn(Set<Long> values);

  ImageQueryBuilder limit(int offset, int nResults);

  ImageQueryBuilder limit(int nResults);

  ImageQueryBuilder groupBy(Image._Fields... fields);

  IOrderedImageQueryBuilder order();

  IOrderedImageQueryBuilder order(QueryOrder queryOrder);

  IOrderedImageQueryBuilder orderById();

  IOrderedImageQueryBuilder orderById(QueryOrder queryOrder);

  ImageQueryBuilder userId(Integer value);

  ImageQueryBuilder whereUserId(IWhereOperator<Integer> operator);

  IOrderedImageQueryBuilder orderByUserId();

  IOrderedImageQueryBuilder orderByUserId(QueryOrder queryOrder);
}
