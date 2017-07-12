package com.rapleaf.jack.store.executors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.store.JsTable;

abstract class BaseInquirerExecutor<TF, TL, E extends BaseInquirerExecutor<TF, TL, E>> extends BaseExecutor<TF, TL> {

  final Set<String> selectedKeys;

  BaseInquirerExecutor(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
    this.selectedKeys = Sets.newHashSet();
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
