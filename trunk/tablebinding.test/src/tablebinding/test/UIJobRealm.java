package tablebinding.test;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class UIJobRealm extends Realm {
	
	private Display display;

public UIJobRealm(Display display) {
	this.display = display;
}


public boolean isCurrent() {
	return Display.getCurrent() == display;
}

public void asyncExec(final Runnable runnable) {
	if (!display.isDisposed()) {
		UIJob uiJob = new UIJob(display, "UIJobRealm") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				runnable.run();
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}
}

public int hashCode() {
	return (display == null) ? 0 : display.hashCode();
}

public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	final UIJobRealm other = (UIJobRealm) obj;
	if (display == null) {
		if (other.display != null)
			return false;
	} else if (!display.equals(other.display))
		return false;
	return true;
}

}
