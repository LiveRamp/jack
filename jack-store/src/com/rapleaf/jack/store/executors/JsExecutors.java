package com.rapleaf.jack.store.executors;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;

public class JsExecutors {

  private final JsTable table;
  private final Long executionRecordId;

  public JsExecutors(JsTable table, Long executionRecordId) {
    this.table = table;
    this.executionRecordId = executionRecordId;
  }

  /**
   * Query key value pairs under the current record.
   *
   * @return {@link JsRecord}
   */
  public RecordReader read() {
    return new RecordReader(table, executionRecordId);
  }

  /**
   * Insert or update key value pairs under the current record.
   *
   * @return {@link JsRecord}
   */
  public RecordUpdater update() {
    return new RecordUpdater(table, executionRecordId);
  }

  /**
   * Delete key value pairs under the current record.
   * Delete the current record.
   *
   * @return {@link Void}
   */
  public RecordDeleter delete() {
    return new RecordDeleter(table, executionRecordId);
  }

  /**
   * Create a new record under the current record.
   * Insert key value pairs under the new record.
   *
   * @return {@link JsRecord}
   */
  public SubRecordCreator createSubRecord() {
    return new SubRecordCreator(table, executionRecordId);
  }

  /**
   * Read the sub records under the current record.
   * The result set does not include key value pairs directly under the current record.
   *
   * @return {@link JsRecords} representing the fetched sub records
   */
  public SubRecordReader readSubRecords() {
    return new SubRecordReader(table, executionRecordId);
  }

  /**
   * Query the sub records under the current record.
   * The result set does not include key value pairs directly under the current record.
   *
   * @return {@link JsRecords} representing the fetched sub records
   */
  public SubRecordInquirer querySubRecords() {
    return new SubRecordInquirer(table, executionRecordId);
  }

  /**
   * Insert or update key value pairs in the sub records under the current record.
   * Key value pairs directly under the current record will not be affected.
   *
   * @return {@link JsRecords} representing the updated sub records
   */
  public SubRecordUpdater updateSubRecords() {
    return new SubRecordUpdater(table, executionRecordId);
  }

  /**
   * Delete sub records under the current record.
   *
   * @return {@link Void}
   */
  public SubRecordDeleter deleteSubRecords() {
    return new SubRecordDeleter(table, executionRecordId);
  }

}
