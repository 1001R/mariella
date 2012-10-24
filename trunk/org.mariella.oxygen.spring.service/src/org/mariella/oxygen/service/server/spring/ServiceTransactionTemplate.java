package org.mariella.oxygen.service.server.spring;


import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class ServiceTransactionTemplate {

private TransactionTemplate springTransactionTemplate;
private ServiceExecutionContextHolder serviceExecutionContextHolder;

public Object execute(TransactionCallback action) throws TransactionException {
	try {
		return springTransactionTemplate.execute(action);
	} finally {
		serviceExecutionContextHolder.getContext().clearTransactionCache();
	}
}

public ServiceExecutionContextHolder getServiceExecutionContextHolder() {
	return serviceExecutionContextHolder;
}

public void setServiceExecutionContextHolder(ServiceExecutionContextHolder serviceExecutionContextHolder) {
	this.serviceExecutionContextHolder = serviceExecutionContextHolder;
}

public TransactionTemplate getSpringTransactionTemplate() {
	return springTransactionTemplate;
}

public void setSpringTransactionTemplate(TransactionTemplate springTransactionTemplate) {
	this.springTransactionTemplate = springTransactionTemplate;
}



}
