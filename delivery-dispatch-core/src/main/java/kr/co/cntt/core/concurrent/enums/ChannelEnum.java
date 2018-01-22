package kr.co.cntt.core.concurrent.enums;

public enum ChannelEnum {
	WEB(1),
	MOBILE_WEB(2),
	APP(3),
	POS(4),
	ADMIN(5)
	;
	
	private int code;
	
	private ChannelEnum(int code) {
		this.setCode(code);
	}
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
}
