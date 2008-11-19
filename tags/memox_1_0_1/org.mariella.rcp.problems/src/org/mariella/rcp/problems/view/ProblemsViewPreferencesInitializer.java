package org.mariella.rcp.problems.view;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemsPlugin;


public class ProblemsViewPreferencesInitializer extends AbstractPreferenceInitializer {

@Override
public void initializeDefaultPreferences() {
	IEclipsePreferences defaults = new DefaultScope().getNode(ProblemsPlugin.PLUGIN_ID);
	defaults.putBoolean(ProblemsViewPreferences.SHOW_ALL_PROBLEMS, true);
	Collection<ProblemCategory> shownCategories = new HashSet<ProblemCategory>();
	{
		shownCategories.add(ProblemCategory.ERROR);
		shownCategories.add(ProblemCategory.WARNING);
	}

	defaults.put(ProblemsViewPreferences.SHOWN_PROBLEM_CATEGORIES, ProblemsViewPreferences.shownCategoriesToString(shownCategories));
}

}
