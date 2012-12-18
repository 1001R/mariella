package org.mariella.oxygen.service.server.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

public class CommonLoggingAdvice implements MethodBeforeAdvice, ThrowsAdvice {

public void before(Method method, Object[] args, Object target) throws Throwable {
	Logger log = Logger.getLogger(target.getClass().getName());
	if (log.isLoggable(Level.FINE))
		log.fine("invoke " + method + " of " + target + " with arguments " + Arrays.toString(args));
}

public void afterThrowing(Method method, Object[] args, Object target, Throwable ex) {
	Logger log = Logger.getLogger(target.getClass().getName());
	if (log.isLoggable(Level.FINE))
		log.fine("exception during invocation of " + method + " of " + target + " with arguments " + Arrays.toString(args));
}
}
