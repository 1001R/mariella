/*******************************************************************************
 * Copyright (c) 2005 John J. Franey
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.mariella.logging;

import java.text.MessageFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * jdk4 logging handler that publishes to eclipse plugin's ILog
 * @author John J. Franey
 *
 */
public class PluginLogHandler extends Handler {

	private ConsoleHandler consoleHandler = new ConsoleHandler();
	private String symbolicName;
	
	public void publish(LogRecord record) {
		consoleHandler.publish(record);
		
		Bundle b = Platform.getBundle(getSymbolicName());
		if(b == null) {
			String m = MessageFormat.format("Plugin: {0} not found.", new Object [] {getSymbolicName()});
			this.getErrorManager().error(m, null, ErrorManager.GENERIC_FAILURE);	
			return;
		}
		ILog log = Platform.getLog(b);
		
		Status s = new Status(getSeverity(record),
				getSymbolicName(),
				getCode(record),
				getFormatter().format(record),
				record.getThrown());
		
		log.log(s);
	}
	
	public PluginLogHandler() {
		super();
		
		initDefaults();
	}
	
	private void initDefaults() {
		initSymbolicName();
		initLevel();
		initFilter();
		initFormatter();
	}
	
	private void initSymbolicName() {
		LogManager lm = LogManager.getLogManager();
		setSymbolicName(lm.getProperty(this.getClass().getName() + ".loggingPlugin"));
		if (getSymbolicName() == null)
			throw new IllegalStateException(getClass().getName() + ".loggingPlugin property not set");
	}
	
	private void initLevel() {
		LogManager lm = LogManager.getLogManager();
		String lvlName = lm.getProperty(this.getClass().getName() + ".level");
		if(lvlName == null) {
			lvlName = "OFF";
		}
		
		Level lvl = Level.parse(lvlName);
		setLevel(lvl);
		
	}
	
	private void initFilter() {
		LogManager lm = LogManager.getLogManager();
		String fltrName = lm.getProperty(this.getClass().getName() + ".filter");
		if(fltrName == null) {
			return;
		}
		
		
		try {
			Class fltrClass = Class.forName(fltrName);
			Filter fltr = (Filter)fltrClass.newInstance();
			this.setFilter(fltr);
		} catch(Exception e) {
			String m = MessageFormat.format("Filter: {0} cannot instantiate.",
					new Object [] {fltrName});
			this.getErrorManager().error(m,	null, ErrorManager.GENERIC_FAILURE);
			
		}
		
	}
	
	private void initFormatter() {
		LogManager lm = LogManager.getLogManager();
		String frmttrName = lm.getProperty(this.getClass().getName() + ".formatter");
		if(frmttrName == null) {
			frmttrName = "java.util.logging.SimpleFormatter";
		}
		try {
			Class frmattrClass = Class.forName(frmttrName);
			Formatter frmttr = (Formatter)frmattrClass.newInstance();
			this.setFormatter(frmttr);
		} catch (Exception e) {
			String m = MessageFormat.format("Formatter: {0} cannot instantiate.", 
					new Object[] {frmttrName});
			this.getErrorManager().
			error(m, null, ErrorManager.GENERIC_FAILURE);
		} 
		
	}

	/**
	 * map jdk 1.4 LogRecord to eclipse Status code
	 * @param record
	 * @return
	 */
	private int getCode(LogRecord record) {
		return 0;
	}

	/**
	 * map jdk1.4 LogRecord to eclise Status severity
	 * @param record
	 * @return
	 */
	private int getSeverity(LogRecord record) {
		Level level = record.getLevel();
		
		if(level == Level.SEVERE)
			return IStatus.ERROR;
		else if(level == Level.WARNING)
			return IStatus.WARNING;
		else if(level == Level.INFO)
			return IStatus.INFO;
		else // config, fine, finer, finest
			return IStatus.OK;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public void flush() {
		// no buffer to flush
	}

	public void close() throws SecurityException {
		// no resources to close
	}

}
