package kr.co.deliverydispatch.enums;

/**
 * session enum 객체
 * @author su
 *
 */
public enum SessionEnum {
	LOGIN_SESSION("login"),
	JOIN_AGREE_SESSION("join_agree"),
	JOIN_SUCCESS_SESSION("join_success");
	
	String sessionKey;
	
	public String getSessionKey() {
		return sessionKey;
	}

	private SessionEnum(final String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
}
