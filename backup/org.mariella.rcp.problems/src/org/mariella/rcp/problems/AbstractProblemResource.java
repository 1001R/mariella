package org.mariella.rcp.problems;


public abstract class AbstractProblemResource implements ProblemResource {

private ProblemsProvider provider;

public ProblemsProvider getProvider() {
	return provider;
}

public void setProvider(ProblemsProvider provider) {
	this.provider = provider;
}

/**
 * Can be ovverridden if standard open editor behaviour
 * is not enough.
 * 
 * @return
 */
public ProblemResourceOpenHandler getResourceOpenHandler() {
	return null;
}

public abstract String getDescription();

@Override
public abstract boolean equals(Object obj);

@Override
public abstract int hashCode();

}
