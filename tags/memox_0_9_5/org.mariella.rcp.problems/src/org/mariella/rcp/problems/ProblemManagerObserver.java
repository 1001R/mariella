package org.mariella.rcp.problems;

public interface ProblemManagerObserver {
void problemListChanged(ProblemManager mgr);
void selectedProblemResourceChanged(ProblemResource problemResource);
}
