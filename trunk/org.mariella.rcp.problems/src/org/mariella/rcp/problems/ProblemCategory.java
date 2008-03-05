package org.mariella.rcp.problems;

public enum ProblemCategory {
WARNING,
ERROR;

public Integer getDefaultOrder() {
	if (this == ERROR) return 0;
	else return 1;
}

public static ProblemCategory fromString(String str) {
	if ("WARNING".equals(str))
		return WARNING;
	else
		return ERROR;
}

}
