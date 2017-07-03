package com.rapleaf.jack.store.executors2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.store.JsTable;

abstract class BaseInquirerExecutor2<T, E extends BaseInquirerExecutor2<T, E>> extends BaseExecutor2<T> {

  final Set<String> selectedKeys = Sets.newHashSet();

  BaseInquirerExecutor2(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  abstract E getSelf();

  public E selectKey(String key, String... otherKeys) {
    this.selectedKeys.add(key);
    this.selectedKeys.addAll(Arrays.asList(otherKeys));
    return getSelf();
  }

  public E selectKey(Collection<String> keys) {
    selectedKeys.addAll(keys);
    return getSelf();
  }

}
