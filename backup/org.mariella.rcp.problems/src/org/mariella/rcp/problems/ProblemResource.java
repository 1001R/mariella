package org.mariella.rcp.problems;

public interface ProblemResource {
ProblemsProvider getProvider();
ProblemResourceOpenHandler getResourceOpenHandler();
String getDescription();
}
