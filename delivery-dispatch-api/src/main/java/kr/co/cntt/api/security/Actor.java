package kr.co.cntt.api.security;

import java.util.UUID;

public class Actor {

	private String loginId;
	private String loginPw;
	private String uuid;
	private String time;
	private String token;
	
	public Actor(String loginId, String loginPw) {
		this.loginId = loginId;
		this.loginPw = loginPw;
		this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
		this.time = String.valueOf(System.currentTimeMillis());
		this.token = getTime();
	}

	public String getLoginId() {
		return loginId;
	}

	public String getLoginPw() {
		return loginPw;
	}

	public String getUsername() {
		//return this.uuid;
		return this.loginId;
	}
	
	public String getPassword() {
		//return this.token;
		return this.loginPw;
	}

	public String getTime() {
		return this.time;
	}

	public String getUuid() {
		return this.uuid;
	}
}
