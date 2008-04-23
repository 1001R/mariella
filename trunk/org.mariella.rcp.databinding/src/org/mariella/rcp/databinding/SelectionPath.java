package org.mariella.rcp.databinding;

public class SelectionPath {

Object[] qualifiers;

public SelectionPath(Object ... qualifiers) {
	this.qualifiers = qualifiers;
}

public SelectionPath(Object[] qualifiers, Object ... qualifiers2) {
	this.qualifiers = new Object[qualifiers.length + qualifiers2.length];
	System.arraycopy(qualifiers, 0, this.qualifiers, 0, qualifiers.length);
	System.arraycopy(qualifiers2, 0, this.qualifiers, qualifiers.length, qualifiers2.length);
}

public Object[] getQualifiers() {
	return qualifiers;
}

}
