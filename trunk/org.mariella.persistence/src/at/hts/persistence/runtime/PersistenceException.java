package at.hts.persistence.runtime;

public class PersistenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

public PersistenceException(Throwable t) {
	super(t);
}
	
}