package org.mariella.cat.ui.editcontext;

import org.mariella.cat.ui.editcontext.RefreshContext.ChangeOp;

public interface IRefreshContextExt {
	
	void needRefresh(RefreshTarget refreshTarget, ChangeOp operation, Object element, String propertyName, Object value);
	
	void doRefresh(RefreshTarget refreshTarget);

}
