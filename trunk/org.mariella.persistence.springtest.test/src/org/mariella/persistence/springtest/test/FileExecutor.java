package org.mariella.persistence.springtest.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class FileExecutor {
	private final InputStream inputStream;
	private final Connection connection;
	
	private StringBuilder currentCommand = new StringBuilder();
	private boolean multilineComment = false;
	private boolean stopOnError = true;
	
public FileExecutor(InputStream inputStream, Connection connection, boolean stopOnError) {
	super();
	this.inputStream = inputStream;
	this.connection = connection;
	this.stopOnError = stopOnError;
}
	
public void execute() {
	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	try {
		try {
			while(true) {
				String line = reader.readLine();
				if(line == null) {
					break;
				} else {
					processLine(line);
				}
			}
		} finally {
			inputStream.close();
		}
	} catch(IOException e) {
		throw new RuntimeException(e);
	}

}

private void processLine(String line) {
	if(currentCommand.length() > 0) {
		currentCommand.append("\n");
	}
	boolean commentStart = false;
	boolean mlCommentStart = false;
	boolean mlCommentEnd = false;
	for(char ch : line.toCharArray()) {
		if(multilineComment) {
			if(mlCommentEnd & ch == '/') {
				mlCommentEnd = false;
				multilineComment = false;
			} else if(ch == '*'){
				mlCommentEnd = true;
			}
		} else {
			if(ch == '/') {
				mlCommentStart = true;
			} else {
				if(mlCommentStart) {
					mlCommentStart = false;
					if(ch == '*') {
						multilineComment = true;
					} else {
						currentCommand.append('/');
					}
				} else {
					if(ch == '-') {
						if(commentStart) {
							break;
						} else {
							commentStart = true;
						}
					} else {
						if(commentStart) {
							currentCommand.append("-");
							commentStart = false;
						} else if(ch == ';') {
							executeCurrentCommand();
							break;
						} 
						currentCommand.append(ch);
					}
				}
			}
		}
	}
}

private void executeCurrentCommand() {
	if(currentCommand.toString().trim().length() > 0) {
		try {
			PreparedStatement ps = connection.prepareStatement(currentCommand.toString());
			try {
				ps.execute();
			} finally {
				ps.close();
			}
		} catch(SQLException e) {
			if(stopOnError) {
				throw new RuntimeException(e);
			}
		}
	} 
	currentCommand = new StringBuilder();
}

}
