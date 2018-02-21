package com.rapleaf.jack.store.json;

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

final class TuplePaths {

  private TuplePaths() {
  }

  static TuplePath create(String path) {
    String[] listPathSplits = path.split(Pattern.quote(JsonDbConstants.LIST_PATH_SEPARATOR));
    if (listPathSplits.length == 3 && NumberUtils.isDigits(listPathSplits[1]) && NumberUtils.isDigits(listPathSplits[2])) {
      int index = Integer.valueOf(listPathSplits[1]);
      int size = Integer.valueOf(listPathSplits[2]);
      if (listPathSplits[0].equals(JsonDbConstants.KEYLESS_ARRAY_NAME)) {
        return new ArrayPath(Optional.empty(), index, size);
      } else {
        return new ArrayPath(Optional.of(listPathSplits[0]), index, size);
      }
    } else {
      return new ElementPath(path);
    }
  }

}
