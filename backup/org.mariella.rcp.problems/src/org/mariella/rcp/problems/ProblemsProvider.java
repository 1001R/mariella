package org.mariella.rcp.problems;

import java.util.List;

public interface ProblemsProvider {

List<ProblemResource> getProblemResources();

void addProblems(ProblemManager problemMgr, ProblemResource resource);

}
