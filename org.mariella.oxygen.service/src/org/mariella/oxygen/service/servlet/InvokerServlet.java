package org.mariella.oxygen.service.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mariella.oxygen.service.Activator;
import org.mariella.oxygen.service.invocation.http.HttpServiceInvoker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class InvokerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String PARAM_TARGET_BUNDLE = "targetBundle";
	private static final String PARAM_VERSION_RANGE = "versionRange";
	private String targetBundleName;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		targetBundleName = config.getInitParameter(PARAM_TARGET_BUNDLE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// filter service name
			String serviceName = request.getRequestURI();
			while(serviceName.endsWith("/")) {
				serviceName = serviceName.substring(0, serviceName.length() - 1);
			}
			serviceName = serviceName.substring(serviceName.lastIndexOf('/') + 1);

			// resolve version range for service
			String versionRange = getVersionRange(request);
//			System.out.println("-> try to invoke service '" + serviceName + "' with versionRange '" + versionRange + "'.");

			// resolve target bundle (for specified version)
			Bundle targetBundle = getTargetBundle(versionRange);
//			System.out.println("-> invoking service '" + serviceName + "' no bundle '" + targetBundle + "'.");

			// resolve + invoke remote service
			HttpServiceInvoker remoteService = getRemoteService(targetBundle, serviceName);
			if(remoteService == null) {
				throw new RuntimeException("Bundle '" + targetBundle + "' has no serivce " +
						"invoker registered for name '" + serviceName + "'.");
			}
			remoteService.invoke(request, response);

		} catch (Exception ex) {
			throw new ServletException(ex);
		}

	}

	private String getVersionRange(HttpServletRequest request) {
		String versionRange = request.getHeader(PARAM_VERSION_RANGE);
		if(versionRange == null) {
			versionRange = request.getParameter(PARAM_VERSION_RANGE);
		}
		return versionRange;
	}

	private Bundle getTargetBundle(String versionRange) {
		final BundleContext bundleContext = Activator.getContext();
		if(bundleContext == null) {
			throw new RuntimeException("Bundle context not initialized.");
		}
		if(targetBundleName != null) {
			final ServiceReference serviceRef = bundleContext.getServiceReference(PackageAdmin.class.getName());
			if(serviceRef == null) {
				throw new RuntimeException("Service '" + PackageAdmin.class.getName() + "' not running.");
			}
			final PackageAdmin packageAdmin = (PackageAdmin) bundleContext.getService(serviceRef);
			final Bundle[] bundles = packageAdmin.getBundles(targetBundleName, versionRange);
			if(bundles == null || bundles.length == 0) {
				throw new RuntimeException("No bundles resolved for symbolic name '" + targetBundleName + "'.");
			}
			return bundles[0];
		} else {
			return bundleContext.getBundle();
		}
	}

	private HttpServiceInvoker getRemoteService(Bundle bundle, String serviceName) {
		final ServiceReference[] servicesReferences = bundle.getRegisteredServices();
		if(servicesReferences != null && servicesReferences.length > 0) {
			for(ServiceReference serviceReference : servicesReferences) {
				if(HttpServiceInvoker.class.getName().equals(((String[]) serviceReference.getProperty("objectClass"))[0])
						&& serviceName.equals(serviceReference.getProperty("serviceName"))) {
					return (HttpServiceInvoker) bundle.getBundleContext().getService(serviceReference);
				}
			}
		}
		return null;
	}

}
