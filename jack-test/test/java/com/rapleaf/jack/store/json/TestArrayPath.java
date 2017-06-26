package com.rapleaf.jack.store.json;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestArrayPath extends BaseJsonTestCase {

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
