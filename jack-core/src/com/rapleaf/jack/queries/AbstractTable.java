package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public class AbstractTable<A extends AttributesWithId, M extends ModelWithId> implements Table<A, M> {
  protected final String name;
  protected final String alias;
  protected final Class<A> attributesType;
  protected final Class<M> modelType;
  protected final Set<Column> allColumns;

  protected AbstractTable(String name, String alias, Class<A> attributesType, Class<M> modelType) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Table name cannot be null or empty.");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(alias), "Table alias cannot be null or empty.");
    this.name = name;
    this.alias = alias;
    this.attributesType = attributesType;
    this.modelType = modelType;
    this.allColumns = Sets.newHashSet();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Set<Column> getAllColumns() {
    return allColumns;
  }

  @Override
  public String getSqlKeyword() {
    return name + " AS " + alias;
  }

  public Class<A> getAttributesType() {
    return attributesType;
  }

  @Override
  public Class<M> getModelType() {
    return modelType;
  }

  @Override
  public TableReference with(final IndexHint indexHint, final IndexHint... indexHints) {
    Set<IndexHint> indexHintList = Sets.newHashSet(indexHint);
    indexHintList.addAll(Arrays.asList(indexHints));
    return new SingleTableReference(this, IndexHint.validate(indexHintList));
  }

}
