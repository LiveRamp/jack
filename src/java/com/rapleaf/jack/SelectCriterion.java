package com.rapleaf.jack;

public class SelectCriterion {

  private Enum selectedField;

  public SelectCriterion(Enum selectedField) {
    this.selectedField = selectedField;
  }

  public Enum getSelectedField() {
    return selectedField;
  }

  public String getSqlClause() {
    return (selectedField.name());
  }
}
