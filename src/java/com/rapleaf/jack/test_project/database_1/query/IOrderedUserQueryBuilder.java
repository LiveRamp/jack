package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.User;

public interface IOrderedUserQueryBuilder extends IOrderedQueryBuilder<User> {
  IOrderedUserQueryBuilder select(User._Fields... fields);

  IOrderedUserQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IOrderedUserQueryBuilder id(Long value);

  IOrderedUserQueryBuilder idIn(Set<Long> values);

  IOrderedUserQueryBuilder limit(int offset, int nResults);

  IOrderedUserQueryBuilder limit(int nResults);

  IOrderedUserQueryBuilder groupBy(User._Fields... fields);

  IOrderedUserQueryBuilder order();

  IOrderedUserQueryBuilder order(QueryOrder queryOrder);

  IOrderedUserQueryBuilder orderById();

  IOrderedUserQueryBuilder orderById(QueryOrder queryOrder);

  IOrderedUserQueryBuilder handle(String value);

  IOrderedUserQueryBuilder whereHandle(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByHandle();

  IOrderedUserQueryBuilder orderByHandle(QueryOrder queryOrder);

  IOrderedUserQueryBuilder createdAtMillis(Long value);

  IOrderedUserQueryBuilder whereCreatedAtMillis(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderByCreatedAtMillis();

  IOrderedUserQueryBuilder orderByCreatedAtMillis(QueryOrder queryOrder);

  IOrderedUserQueryBuilder numPosts(Integer value);

  IOrderedUserQueryBuilder whereNumPosts(IWhereOperator<Integer> operator);

  IOrderedUserQueryBuilder orderByNumPosts();

  IOrderedUserQueryBuilder orderByNumPosts(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someDate(Long value);

  IOrderedUserQueryBuilder whereSomeDate(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDate();

  IOrderedUserQueryBuilder orderBySomeDate(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someDatetime(Long value);

  IOrderedUserQueryBuilder whereSomeDatetime(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDatetime();

  IOrderedUserQueryBuilder orderBySomeDatetime(QueryOrder queryOrder);

  IOrderedUserQueryBuilder bio(String value);

  IOrderedUserQueryBuilder whereBio(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByBio();

  IOrderedUserQueryBuilder orderByBio(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someBinary(byte[] value);

  IOrderedUserQueryBuilder whereSomeBinary(IWhereOperator<byte[]> operator);

  IOrderedUserQueryBuilder orderBySomeBinary();

  IOrderedUserQueryBuilder orderBySomeBinary(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someFloat(Double value);

  IOrderedUserQueryBuilder whereSomeFloat(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeFloat();

  IOrderedUserQueryBuilder orderBySomeFloat(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someDecimal(Double value);

  IOrderedUserQueryBuilder whereSomeDecimal(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeDecimal();

  IOrderedUserQueryBuilder orderBySomeDecimal(QueryOrder queryOrder);

  IOrderedUserQueryBuilder someBoolean(Boolean value);

  IOrderedUserQueryBuilder whereSomeBoolean(IWhereOperator<Boolean> operator);

  IOrderedUserQueryBuilder orderBySomeBoolean();

  IOrderedUserQueryBuilder orderBySomeBoolean(QueryOrder queryOrder);
}
