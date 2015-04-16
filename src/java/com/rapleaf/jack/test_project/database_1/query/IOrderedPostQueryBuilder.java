package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.Post;

public interface IOrderedPostQueryBuilder extends IPostQueryBuilder, IOrderedQueryBuilder<Post> {

}
