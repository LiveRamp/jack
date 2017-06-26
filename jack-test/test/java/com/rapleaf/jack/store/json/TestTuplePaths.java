package com.rapleaf.jack.store.json;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTuplePaths extends BaseJsonTestCase {

  @Test
  public void testElementPath() throws Exception {
    ElementPath path = new ElementPath(ELEMENT_PATH_NAME);
    assertEquals(path, TuplePaths.create(path.toString()));
  }

  @Test
  public void testArrayPath() throws Exception {
    ArrayPath path = new ArrayPath(Optional.of(ARRAY_PATH_NAME), 0, 3);
    assertEquals(path, TuplePaths.create(path.toString()));
  }

  @Test
  public void testKeylessArrayPath() throws Exception {
    ArrayPath path = new ArrayPath(Optional.empty(), 0, 3);
    assertEquals(path, TuplePaths.create(path.toString()));
  }

  @Test
  public void testPathCreationFromString() throws Exception {
    // element path
    assertTrue(TuplePaths.create("array") instanceof ElementPath);
    assertTrue(TuplePaths.create("") instanceof ElementPath);
    // only strings that match /.*\|\d+\|\d+/ will be matched to ArrayPath
    // all else are matched to ElementPath
    assertTrue(TuplePaths.create("|") instanceof ElementPath);
    assertTrue(TuplePaths.create("||") instanceof ElementPath);
    assertTrue(TuplePaths.create("||0") instanceof ElementPath);
    assertTrue(TuplePaths.create("|0|") instanceof ElementPath);
    assertTrue(TuplePaths.create("|||") instanceof ElementPath);

    // array path
    assertTrue(TuplePaths.create("array|10|15") instanceof ArrayPath);
    assertTrue(TuplePaths.create("|10|15") instanceof ArrayPath);
  }

}
