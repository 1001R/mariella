package org.mariella.rcp.problems.view;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemsPlugin;


public class ProblemsViewPreferences {
public static final String SHOW_ALL_PROBLEMS = "prefs_problemsview_show_all_problems";
public static final String SHOWN_PROBLEM_CATEGORIES = "prefs_problemsview_shown_categories";

Collection<ProblemCategory> shownCategories;
boolean showAllProblems;

public void load() {
	showAllProblems = getPreferencesStore().getBoolean(SHOW_ALL_PROBLEMS);
	shownCategories = categoriesFromString(getPreferencesStore().getString(SHOWN_PROBLEM_CATEGORIES));	
}

public void save() {
	getPreferencesStore().setValue(ProblemsViewPreferences.SHOW_ALL_PROBLEMS, showAllProblems);
	getPreferencesStore().setValue(ProblemsViewPreferences.SHOWN_PROBLEM_CATEGORIES, ProblemsViewPreferences.shownCategoriesToString(shownCategories));
	try {
		getPreferencesStore().save();
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}

public static ScopedPreferenceStore getPreferencesStore() {
	return new ScopedPreferenceStore(new ConfigurationScope(), ProblemsPlugin.PLUGIN_ID);
}

public static Collection<ProblemCategory> categoriesFromString(String str) {
	StringTokenizer t = new StringTokenizer(str, ",");
	Collection<ProblemCategory> cats = new HashSet<ProblemCategory>();
	while (t.hasMoreTokens()) {
		String x = t.nextToken();
		cats.add(ProblemCategory.fromString(x));
	}
	return cats;
}

public static String shownCategoriesToString(Collection<ProblemCategory> cats) {
	String str = "";
	for (Iterator<ProblemCategory> i = cats.iterator(); i.hasNext();) {
		ProblemCategory c = i.next();
		str += c.toString();
		if (i.hasNext())
			str += ",";
	}
	return str;
}

}
