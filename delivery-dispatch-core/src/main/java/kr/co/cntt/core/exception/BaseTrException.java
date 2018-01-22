package kr.co.cntt.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseTrException extends Exception {
	private static final long serialVersionUID = 2281009760124880192L;

	public BaseTrException() {
		super();
	}
	
	public BaseTrException(String message) {
		super(message);
	}
	
	public BaseTrException(Throwable ex) {
		super(ex);
	}
	
	public BaseTrException(String message, Throwable ex) {
		super(message, ex);
	}
	
	/** forward URL **/
	protected String forwardUrl;
	/** error code */
	protected String errorCode;
	/** error message */
	protected String errorMessage;
}
