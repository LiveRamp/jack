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
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;

public interface IUserQueryBuilder {

  IUserQueryBuilder select(User._Fields... fields);

  IUserQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  IUserQueryBuilder id(Long value);

  IUserQueryBuilder idIn(Set<Long> values);

  IUserQueryBuilder limit(int offset, int nResults);

  IUserQueryBuilder limit(int nResults);

  IUserQueryBuilder groupBy(User._Fields... fields);

  IOrderedUserQueryBuilder order();

  IOrderedUserQueryBuilder order(QueryOrder queryOrder);

  IOrderedUserQueryBuilder orderById();

  IOrderedUserQueryBuilder orderById(QueryOrder queryOrder);

  IUserQueryBuilder handle(String value);

  IUserQueryBuilder whereHandle(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByHandle();

  IOrderedUserQueryBuilder orderByHandle(QueryOrder queryOrder);

  IUserQueryBuilder createdAtMillis(Long value);

  IUserQueryBuilder whereCreatedAtMillis(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderByCreatedAtMillis();

  IOrderedUserQueryBuilder orderByCreatedAtMillis(QueryOrder queryOrder);

  IUserQueryBuilder numPosts(Integer value);

  IUserQueryBuilder whereNumPosts(IWhereOperator<Integer> operator);

  IOrderedUserQueryBuilder orderByNumPosts();

  IOrderedUserQueryBuilder orderByNumPosts(QueryOrder queryOrder);

  IUserQueryBuilder someDate(Long value);

  IUserQueryBuilder whereSomeDate(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDate();

  IOrderedUserQueryBuilder orderBySomeDate(QueryOrder queryOrder);

  IUserQueryBuilder someDatetime(Long value);

  IUserQueryBuilder whereSomeDatetime(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDatetime();

  IOrderedUserQueryBuilder orderBySomeDatetime(QueryOrder queryOrder);

  IUserQueryBuilder bio(String value);

  IUserQueryBuilder whereBio(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByBio();

  IOrderedUserQueryBuilder orderByBio(QueryOrder queryOrder);

  IUserQueryBuilder someBinary(byte[] value);

  IUserQueryBuilder whereSomeBinary(IWhereOperator<byte[]> operator);

  IOrderedUserQueryBuilder orderBySomeBinary();

  IOrderedUserQueryBuilder orderBySomeBinary(QueryOrder queryOrder);

  IUserQueryBuilder someFloat(Double value);

  IUserQueryBuilder whereSomeFloat(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeFloat();

  IOrderedUserQueryBuilder orderBySomeFloat(QueryOrder queryOrder);

  IUserQueryBuilder someDecimal(Double value);

  IUserQueryBuilder whereSomeDecimal(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeDecimal();

  IOrderedUserQueryBuilder orderBySomeDecimal(QueryOrder queryOrder);

  IUserQueryBuilder someBoolean(Boolean value);

  IUserQueryBuilder whereSomeBoolean(IWhereOperator<Boolean> operator);

  IOrderedUserQueryBuilder orderBySomeBoolean();

  IOrderedUserQueryBuilder orderBySomeBoolean(QueryOrder queryOrder);
}
