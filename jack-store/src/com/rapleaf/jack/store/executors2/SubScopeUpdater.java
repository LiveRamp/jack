package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubScopeUpdater extends BaseCreatorExecutor2<JsRecords, SubScopeUpdater> {

  private final Set<Long> subScopeIds = Sets.newHashSet();
  private boolean allowBulkUpdate = false;
  private boolean ignoreInvalidSubScopes = false;

  SubScopeUpdater(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  public SubScopeUpdater whereSubScopeIds(Set<Long> subScopeIds) {
    this.subScopeIds.addAll(subScopeIds);
    return this;
  }

  public SubScopeUpdater allowBulkUpdate() {
    this.allowBulkUpdate = true;
    return this;
  }

  public SubScopeUpdater ignoreInvalidSubScopes() {
    this.ignoreInvalidSubScopes = true;
    return this;
  }

  private static String processKey(String key) {
    String[] paths = key.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR));
    if (paths.length == 1) {
      return key;
    } else {
      return Joiner.on("%.").join(paths) + "%";
    }
  }

  @Override
  SubScopeUpdater getSelf() {
    return this;
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    if (types.isEmpty()) {
      return JsRecords.empty(executionScopeId);
    }

    if (subScopeIds.isEmpty() && !allowBulkUpdate) {
      throw new JackRuntimeException("Bulk update is disabled; either enable it or specify at least one sub scope ID");
    }

    Set<Long> validSubScopeIds = getValidSubScopeIds(db, subScopeIds, ignoreInvalidSubScopes);
    if (validSubScopeIds.isEmpty()) {
      return JsRecords.empty(executionScopeId);
    }

    for (long subScopeId : validSubScopeIds) {
      deleteExistingEntries(db, subScopeId);
      insertNewEntries(db, subScopeId);
    }

    return new SubScopeReader(table, true, executionScopeId)
        .whereSubScopeIds(validSubScopeIds)
        .execute(db);
  }

}
