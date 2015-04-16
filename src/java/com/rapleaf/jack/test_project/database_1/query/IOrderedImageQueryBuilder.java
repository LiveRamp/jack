package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.queries.IOrderedQueryBuilder;
import com.rapleaf.jack.test_project.database_1.models.Image;

public interface IOrderedImageQueryBuilder extends IImageQueryBuilder, IOrderedQueryBuilder<Image> {

}
