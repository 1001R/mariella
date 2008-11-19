package org.mariella.rcp.databinding.internal;


public class IdentityWrapper {
final Object o;

/**
 * @param o
 */
public IdentityWrapper(Object o) {
	this.o = o;
}

/**
 * @return the unwrapped object
 */
public Object unwrap() {
	return o;
}

@Override
public boolean equals(Object obj) {
	if (obj == null || obj.getClass() != IdentityWrapper.class) {
		return false;
	}
	return o == ((IdentityWrapper) obj).o;
}

@Override
public int hashCode() {
	return System.identityHashCode(o);
}

}
