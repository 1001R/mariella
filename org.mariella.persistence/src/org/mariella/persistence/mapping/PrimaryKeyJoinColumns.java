package org.mariella.persistence.mapping;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKeyJoinColumns {
	private List<PrimaryKeyJoinColumn> primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumn>();
	

public void addPrimaryKeyJoinColumn(PrimaryKeyJoinColumn primaryKeyJoinColumn) {
	primaryKeyJoinColumns.add(primaryKeyJoinColumn);
}
	
public List<PrimaryKeyJoinColumn> getPrimaryKeyJoinColumns() {
	return primaryKeyJoinColumns;
}

}
