package com.rapleaf.jack.store.json;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class JsonDbTuple {

  private final List<TuplePath> paths;
  /**
   * When value is present, it represents json value (string, number, boolean)
   * When value is empty, it represents null value (null)
   * When value is null, it represents empty json value ("{}" or "[]")
   * <p>
   * I know, this makes me weep too.
   */
  private final Optional<String> value;

  private JsonDbTuple(List<TuplePath> paths, Optional<String> value) {
    Preconditions.checkArgument(!paths.isEmpty(), "Value path cannot be empty: " + value);
    this.paths = paths;
    this.value = value;
  }

  static JsonDbTuple create(List<TuplePath> paths, String value) {
    return new JsonDbTuple(paths, Optional.of(Preconditions.checkNotNull(value)));
  }

  static JsonDbTuple createNull(List<TuplePath> paths) {
    return new JsonDbTuple(paths, Optional.empty());
  }

  static JsonDbTuple createEmptyObject(List<TuplePath> paths) {
    return new JsonDbTuple(paths, null);
  }

  List<TuplePath> getPaths() {
    return paths;
  }

  Optional<String> getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", Joiner.on(JsonDbConstants.PATH_SEPARATOR).join(paths), value);
  }

}
