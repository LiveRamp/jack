package com.liveramp.jack.testcontainers;

abstract class LazyLoadingSingletonFactory<T> {
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
