package kr.co.cntt.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 6600901981656940565L;

	public BaseException() {
		super();
	}
	
	public BaseException(String message) {
		super(message);
	}
	
	public BaseException(Throwable ex) {
		super(ex);
	}
	
	public BaseException(String message, Throwable ex) {
		super(message, ex);
	}
	
	/** forward URL **/
	protected String forwardUrl;
	/** error code */
	protected String errorCode;
	/** error message */
	protected String errorMessage;
}
