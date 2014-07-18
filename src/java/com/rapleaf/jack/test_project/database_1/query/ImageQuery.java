package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractModelQuery;

import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;


public class ImageQuery extends AbstractModelQuery<Image> {

  public ImageQuery (IImagePersistence caller) {
    super(caller);
  }

  public ImageQuery user_id(Integer value) {
    fieldsMap.put(Image._Fields.user_id, value);
    return this;
  }
}
