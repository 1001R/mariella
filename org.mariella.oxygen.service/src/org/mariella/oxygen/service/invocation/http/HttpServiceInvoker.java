package org.mariella.oxygen.service.invocation.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OSGi service interface for HTTP remote invocation.
 *
 * @author Sasa Teofanovic
 */
public interface HttpServiceInvoker {

	public void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
