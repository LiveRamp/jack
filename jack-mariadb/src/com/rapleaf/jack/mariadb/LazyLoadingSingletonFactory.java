package com.rapleaf.jack.mariadb;

import java.io.Serializable;

public abstract class LazyLoadingSingletonFactory<T> implements Serializable {
  volatile T instance;

  protected abstract T create();

  public T get() {
    if (instance == null) {
      synchronized (this) {
        if (instance == null) {
          instance = create();
        }
      }
    }
    return instance;
  }
}
