package org.mariella.sample.app;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

private static final String PERSPECTIVE_ID = "org.mariella.sample.app.perspective";

public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
}

public String getInitialWindowPerspectiveId() {
	return PERSPECTIVE_ID;
}

@Override
public void eventLoopException(Throwable exception) {
	exception.printStackTrace();
	super.eventLoopException(exception);
}
}
