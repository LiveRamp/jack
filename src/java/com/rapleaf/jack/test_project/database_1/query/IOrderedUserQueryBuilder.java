package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.User;

public interface IOrderedUserQueryBuilder extends IUserQueryBuilder, IOrderedQueryBuilder<User> {

}
