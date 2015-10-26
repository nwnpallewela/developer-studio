package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.exception;

public class MissingAttributeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3330861798440075L;

	public MissingAttributeException() {
	}

	public MissingAttributeException(String message) {
		super(message);
	}

	public MissingAttributeException(Throwable cause) {
		super(cause);
	}

	public MissingAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingAttributeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
