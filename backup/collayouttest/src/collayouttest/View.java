package collayouttest;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mariella.rcp.util.RowLayout;

public class View extends ViewPart {
	public static final String ID = "collayouttest.view";


	public void createPartControl(Composite parent) {
		Composite client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(1, false));
		
		Text text = new Text(client, SWT.MULTI);
		
		Composite test = new Composite(client, SWT.BORDER);
		
		RowLayout rowLayout = new RowLayout();
		test.setLayout(rowLayout);
		
		Button b = new Button(test, SWT.PUSH);
		b.setText("asdf");
		b = new Button(test, SWT.PUSH);
		b.setText("xyu");
		rowLayout.setVisible(b, MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "show 2nd button", "show 2nd button?"));
		
		
	}

	public void setFocus() {
	}
}