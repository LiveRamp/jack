package com.rapleaf.jack.store.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestElementPath extends BaseJsonTestCase {

  @Test
  public void testElementPath() throws Exception {
    TuplePath elementPath = new ElementPath(ELEMENT_PATH_NAME);
    assertEquals(elementPath, TuplePaths.create(elementPath.toString()));
  }

}
