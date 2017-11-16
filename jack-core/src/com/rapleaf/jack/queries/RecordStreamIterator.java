package com.rapleaf.jack.queries;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

class RecordStreamIterator implements Iterator<Record> {
  private Iterator<Record> currentBlockPointer = null;
  private boolean hasNextBlock = true;
  private int offset = 0;
  private int limitIndex = Integer.MAX_VALUE;
  private int blockSize;
  private GenericQuery query;

  RecordStreamIterator(int blockSize, GenericQuery query, Optional<LimitCriterion> preSetLimit) {
    this.blockSize = blockSize;
    this.query = query;
    if (preSetLimit.isPresent()) {
      this.offset = preSetLimit.get().getOffset();
      this.limitIndex = preSetLimit.get().getNResults() + preSetLimit.get().getOffset();
    }
    fetchBlock();
  }

  private void fetchBlock() {
    int numToFetch = Math.min(blockSize, (limitIndex - offset));
    query.limit(offset, numToFetch);
    try {
      List<Record> fetch = query.fetch().getRecords();
      currentBlockPointer = fetch.iterator();
      if (fetch.size() < numToFetch) {
        hasNextBlock = false;
      }
      offset += fetch.size();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean hasNext() {
    if (!currentBlockPointer.hasNext() && hasNextBlock) {
      fetchBlock();
    }
    return currentBlockPointer.hasNext();
  }

  @Override
  public Record next() {
    if (currentBlockPointer.hasNext()) {
      return currentBlockPointer.next();
    } else {
      fetchBlock();
      return currentBlockPointer.next();
    }
  }
}
