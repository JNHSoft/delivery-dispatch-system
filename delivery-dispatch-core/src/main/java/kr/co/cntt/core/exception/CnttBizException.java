package kr.co.cntt.core.exception;

public class CnttBizException extends BaseException {
	/** default serial ID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new runtime exception with <code>null</code> as its detail
	 * message.
	 * 
	 * @param builder
	 */
	private CnttBizException(final Builder builder) {
		super();
		this.setBuilder(builder);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message.
	 * 
	 * @param message
	 *            오류 메시지
	 * @param builder
	 */
	private CnttBizException(final String message, final Builder builder) {
		super(message);
		this.setBuilder(builder);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.
	 * 
	 * @param message
	 *            오류 메시지
	 * @param cause
	 *            발생원인 예외 객체
	 * @param builder
	 */
	private CnttBizException(final String message, final Throwable cause, final Builder builder) {
		super(message, cause);
		this.setBuilder(builder);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.
	 * 
	 * @param cause
	 *            발생원인 예외 객체
	 * @param builder
	 */
	private CnttBizException(final Throwable cause, final Builder builder) {
		super(cause);
		this.setBuilder(builder);
	}

	/**
	 * local field setting
	 * 
	 * @param builder
	 */
	private final void setBuilder(final Builder builder) {
		this.forwardUrl = builder.forwardUrl;
		this.errorCode = builder.errorCode;
	}

	/** constructor */
	@SuppressWarnings("unused")
	private CnttBizException() {
	}


	/**
	 * 생성자
	 * @param message
	 * @param builder
	 * @param forwardUrl
	 * @param errorCode
	 */
	public CnttBizException(final String message, final String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 생성자
	 * @param message
	 * @param errorCode
	 * @param cause
	 * @param forwardUrl
	 * @param builder
	 */
	public CnttBizException(final String message, final String errorCode, final String forwardUrl) {
		super(message);
		this.errorCode = errorCode;
		this.forwardUrl = forwardUrl;
	}
	
	/**
	 * <code>KCPException</code> is the superclass of those RuntimeExceptions
	 * that can be thrown during the normal operation of the Java Virtual
	 * Machine.<br>
	 * A method is not required to declare in its <code>throws</code> clause any
	 * subclasses of <code>CnttBizException</code> that might be thrown
	 * during the execution of the method but not caught.
	 */
	public static class Builder {
		/** 오류 메시지 */
		private String message;
		/** 발생원인 예외 객체 */
		private Throwable cause;
		/** forward URL **/
		private String forwardUrl;
		/** error code */
		private String errorCode;

		/**
		 * Constructs a new runtime exception with <code>null</code> as its
		 * detail message. The cause is not initialized, and may subsequently be
		 * initialized by a call to {@link #initCause}.
		 */
		public Builder() {
		}

		/**
		 * Constructs a new runtime exception with the specified detail message.
		 * The cause is not initialized, and may subsequently be initialized by
		 * a call to {@link #initCause}.
		 * 
		 * @param message
		 *            the detail message
		 */
		public Builder(final String message) {
			this.message = message;
		}

		/**
		 * Constructs a new runtime exception with the specified detail message
		 * and cause.
		 * 
		 * @param message
		 *            the detail message
		 * @param cause
		 *            cause the cause
		 */
		public Builder(final String message, final Throwable cause) {
			this.message = message;
			this.cause = cause;
		}

		/**
		 * Constructs a new runtime exception with the specified detail message
		 * and cause.
		 * 
		 * @param cause
		 *            cause the cause
		 */
		public Builder(final Throwable cause) {
			this.cause = cause;
		}

		/**
		 * forward URL 정보를 설정한 Builder class 반환한다.
		 * 
		 * @param forwardUrl
		 *            forward URL
		 * @return Builder class
		 */
		public Builder forwardUrl(final String forwardUrl) {
			this.forwardUrl = forwardUrl;
			return this;
		}

		/**
		 * error code 값을 설정한 Builder class 반환한다.
		 * 
		 * @param responseCode
		 *            HTTP response code
		 * @return Builder class
		 */
		public Builder responseCode(final String errorCode) {
			this.errorCode = errorCode;
			return this;
		}

		/**
		 * response message 셋팅 후 builder class 반환
		 * @param errorMessage
		 * @return
		 */
		public Builder responseMessage(final String message) {
			this.message = message;
			return this;
		}
		
		/**
		 * Builder class 이용하여 KCPException class 생성한다.
		 * 
		 * @return CnttBizException class
		 */
		public CnttBizException build() {
			if (this.message != null && this.cause != null) {
				return new CnttBizException(this.message, this.cause, this);
			} else if (this.message != null) {
				return new CnttBizException(this.message, this);
			} else if (this.cause != null) {
				return new CnttBizException(this.cause, this);
			} else {
				return new CnttBizException(this);
			}
		}
	}
}
