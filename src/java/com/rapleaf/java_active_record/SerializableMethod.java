package com.rapleaf.java_active_record;

import java.io.Serializable;
import java.lang.reflect.Method;

public final class SerializableMethod implements Serializable {
  private static final Class<?>[] NO_ARGS = new Class<?>[0];
  private transient Method actualMethod;
  private final String methodName;
  private final String className;

  public SerializableMethod(Class klass, String methodName) {
    this.methodName = methodName;
    this.className = klass.getName();
  }

  public Method getMethod() {
    if (actualMethod == null) {
      try {
        actualMethod = Class.forName(className).getMethod(methodName, NO_ARGS);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return actualMethod;
  }
}
