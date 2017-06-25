package com.rapleaf.jack.store.json;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTuplePaths {

  private static final String ELEMENT_PATH_NAME = "test_path_name";
  private static final String ARRAY_PATH_NAME = "test_path_name";
  private static final int ARRAY_INDEX = 1;
  private static final int ARRAY_SIZE = 5;

  @Test
  public void testElementPath() throws Exception {
    TuplePath elementPath = new ElementPath(ELEMENT_PATH_NAME);
    assertEquals(elementPath, TuplePaths.create(elementPath.toString()));
  }

  @Test
  public void testArrayPath() throws Exception {
    TuplePath arrayPath = new ArrayPath(Optional.of(ARRAY_PATH_NAME), ARRAY_INDEX, ARRAY_SIZE);
    assertEquals(arrayPath, TuplePaths.create(arrayPath.toString()));
  }

  @Test
  public void testKeylessArrayPath() throws Exception {
    TuplePath arrayPath = new ArrayPath(Optional.empty(), ARRAY_INDEX, ARRAY_SIZE);
    assertEquals(arrayPath, TuplePaths.create(arrayPath.toString()));
  }

}
