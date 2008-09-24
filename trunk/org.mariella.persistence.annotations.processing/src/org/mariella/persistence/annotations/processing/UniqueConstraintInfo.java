package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.util.Arrays;

import javax.persistence.UniqueConstraint;

public class UniqueConstraintInfo {

UniqueConstraint uniqueConstraint;

public String[] getColumnNames() {
	return uniqueConstraint.columnNames();
}

void setUniqueConstraint(UniqueConstraint uniqueConstraint) {
	this.uniqueConstraint = uniqueConstraint;
}

public void debugPrint(PrintStream out) {
	out.print(" @UniqueContstraint " + Arrays.toString(getColumnNames()));
}

}
