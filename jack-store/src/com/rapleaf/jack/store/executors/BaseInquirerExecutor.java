package com.rapleaf.jack.store.executors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.store.JsTable;

abstract class BaseInquirerExecutor<T, E extends BaseInquirerExecutor<T, E>> extends BaseExecutor<T> {

  final Set<String> selectedKeys = Sets.newHashSet();

  BaseInquirerExecutor(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
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
