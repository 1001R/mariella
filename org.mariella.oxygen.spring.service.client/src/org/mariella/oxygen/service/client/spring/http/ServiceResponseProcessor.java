package org.mariella.oxygen.service.client.spring.http;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mariella.oxygen.basic_impl.OxyEntityManagerImpl;
import org.mariella.oxygen.basic_impl.OxyObjectPoolImpl;
import org.mariella.oxygen.service.server.spring.EntityAsPoolIdentity;
import org.mariella.oxygen.service.server.spring.ServiceResponse;

public final class ServiceResponseProcessor {
	
	class ResolvingObjectInputStream extends ObjectInputStream {
		private final OxyHttpClientServiceInvocationContext context;
		private final ClassLoader classLoader;
		public ResolvingObjectInputStream(InputStream is, OxyHttpClientServiceInvocationContext context) throws IOException {
			super(is);
			this.context = context;
			this.classLoader = context.getClassLoader();
			enableResolveObject(true);
		}
		@Override
		protected Object resolveObject(Object obj) throws IOException {
			if (obj instanceof EntityAsPoolIdentity) {
				EntityAsPoolIdentity id = (EntityAsPoolIdentity)obj;
				return context.getEntityManager().getObjectPool().getEntityStateForPoolId(id.poolIdentity).getEntity();
			}
			return super.resolveObject(obj);
		}
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
		    return Class.forName(desc.getName(), false, classLoader);
		}

	}

protected Object processResponse(OxyHttpClientServiceInvocationContext context, ServiceResponse serviceResponse) throws Exception {
	handleMerge(context, serviceResponse);
	
	ResolvingObjectInputStream rois = new ResolvingObjectInputStream(new ByteArrayInputStream(serviceResponse.getNativeResult()), context);
	return rois.readObject();
}

@SuppressWarnings("unchecked")
protected void handleMerge(OxyHttpClientServiceInvocationContext context, ServiceResponse response) throws Exception {
	if (context.getEntityManager() == null)
		return;
	
	//((OxyObjectPoolImpl)context.getEntityManager().getObjectPool()).assertConsistent();
	
	OxyObjectPoolImpl pool = (OxyObjectPoolImpl)response.getObjectPool();
	pool.setEntityManager((OxyEntityManagerImpl)context.getEntityManager());
	context.getEntityManager().getObjectPool().mergeRelated(response.getObjectPool());
	
	//((OxyObjectPoolImpl)context.getEntityManager().getObjectPool()).assertConsistent();
	
	List<String> history = (List<String>)context.getEntityManager().getObjectPool().getAttribute("history");
	if(history == null) {
		history = new ArrayList<String>();
		
	}
	history.add("merged on client at " + new Date().toString());
}

}
