package org.mariella.oxygen.service.server.spring;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

public class CommonLoggingAdvice implements MethodBeforeAdvice, ThrowsAdvice {

public void before(Method method, Object[] args, Object target) throws Throwable {
	Log log = LogFactory.getLog(target.getClass());
	if (log.isDebugEnabled())
		log.debug("invoke " + method + " of " + target + " with arguments " + Arrays.toString(args));
}

public void afterThrowing(Method method, Object[] args, Object target, Throwable ex) {
	Log log = LogFactory.getLog(target.getClass());
	if (log.isDebugEnabled())
		log.debug("exception during invocation of " + method + " of " + target + " with arguments " + Arrays.toString(args));
}
}
