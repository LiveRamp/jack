package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Iterator;

public class SelectCriterion {

  private ArrayList<Enum> selectedFields;

  public SelectCriterion() {
    this.selectedFields = new ArrayList<Enum>();
  }

  public ArrayList<Enum> getSelectedFields() {
    return selectedFields;
  }

  public void addSelectedField(Enum field) {
    this.selectedFields.add(field);
  }

  public String getSqlClause() {
    StringBuilder sqlClause = new StringBuilder("SELECT ");

    if (selectedFields.isEmpty()) {
      sqlClause.append("*");
      return sqlClause.toString();
    }

    Iterator<Enum> it = selectedFields.iterator();
    while (it.hasNext()) {
      sqlClause.append(it.next());
      if (it.hasNext()) {
        sqlClause.append(", ");
      }
    }
    return sqlClause.toString();
  }
}
