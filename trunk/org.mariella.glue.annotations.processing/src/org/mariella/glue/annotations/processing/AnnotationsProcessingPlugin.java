package org.mariella.glue.annotations.processing;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.ClassFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.mariella.glue.annotations.BindingDomain;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class AnnotationsProcessingPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.mariella.glue.annotations.processing";

	private static AnnotationsProcessingPlugin plugin;
	
public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
}

public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
}

public static AnnotationsProcessingPlugin getDefault() {
	return plugin;
}

public List<BindingDomainInfo> fetchDomainInfos(Bundle bundle) {
	try {
		URL url = FileLocator.resolve(bundle.getResource("."));
		File dir = new File(url.getFile());
		
		List<BindingDomainInfo> domainInfos = new ArrayList<BindingDomainInfo>();
		List<InputStream> classInputStreams = new ArrayList<InputStream>(); 
		readClasses(dir, classInputStreams); 
		for (InputStream is : classInputStreams) {
			DataInputStream dstream = new DataInputStream(is);
			ClassFile cf = null;
			try {
				cf = new ClassFile(dstream);
			} finally {
				dstream.close();
				is.close();
			}
			
			Class<?> clazz = bundle.loadClass(cf.getName());
			for (Field field : clazz.getDeclaredFields()) {
				BindingDomain domainAnno = field.getAnnotation(BindingDomain.class);
				if (domainAnno != null) {
					BindingDomainInfo info = new BindingDomainInfo();
					info.declaringClass = clazz;
					info.attributeName = field.getName();
					info.bindingDomain = domainAnno;
					domainInfos.add(info);
				}
			}
			
			
			for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				BindingDomain domainAnno = null;
				if (prop.getReadMethod() != null && prop.getReadMethod().getDeclaringClass() == clazz) {
					domainAnno = prop.getReadMethod().getAnnotation(BindingDomain.class);
				}
				if (domainAnno == null && prop.getWriteMethod() != null && prop.getWriteMethod().getDeclaringClass() == clazz) {
					domainAnno = prop.getWriteMethod().getAnnotation(BindingDomain.class);
				}
				if (domainAnno != null) {
					BindingDomainInfo info = new BindingDomainInfo();
					info.declaringClass = clazz;
					info.attributeName = prop.getName();
					info.bindingDomain = domainAnno; 
					domainInfos.add(info);
				}
			}
		}
		
		return domainInfos;
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

private void readClasses(File dir, List<InputStream> result) {
	try {
		File[] dirfiles = dir.listFiles();
		for (int i = 0; i < dirfiles.length; i++) {
			if (dirfiles[i].isDirectory()) {
				readClasses(dirfiles[i], result);
			} else {
				if (dirfiles[i].getName().endsWith(".class")) {
					result.add(new FileInputStream(dirfiles[i]));
				}
			}
		}
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

}
