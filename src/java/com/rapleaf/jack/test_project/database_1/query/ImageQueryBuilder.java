package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.SimpleQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;


public class ImageQueryBuilder extends SimpleQueryBuilder<Image> {

  public ImageQueryBuilder (IImagePersistence caller) {
    super(caller);
  }

  public ImageQueryBuilder userId(Integer value) {
    fieldsMap.put(Image._Fields.user_id, value);
    return this;
  }
}
