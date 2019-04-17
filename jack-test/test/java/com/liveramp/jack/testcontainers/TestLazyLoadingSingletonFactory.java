package com.liveramp.jack.testcontainers;

import org.junit.Test;

import com.rapleaf.jack.JackTestCase;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public class TestLazyLoadingSingletonFactory extends JackTestCase {
  @Test
  public void testSingletonFactory() {
    String str = "string value";

    LazyLoadingSingletonFactory<String> factory = new LazyLoadingSingletonFactory<String>() {
      @Override
      protected String create() {
        return str + "";
      }
    };

    String firstResponse = factory.get();
    String secondResponse = factory.get();

    assertSame(firstResponse, secondResponse);
    assertNotSame(str, firstResponse);
    assertNotSame(str, secondResponse);
  }
}
