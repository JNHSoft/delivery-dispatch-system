package kr.co.cntt.api.security;

import java.util.UUID;

public class Actor {

	private String loginId;
	private String loginPw;
	private String uuid;
	private String ip;
	private String time;
	private String mainKey;
	private String token;
	
	public Actor(String loginId, String loginPw, String ip, String serviceKey) {
		this.loginId = loginId;
		this.loginPw = loginPw;
		this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
		this.ip = ip;
		this.time = String.valueOf(System.currentTimeMillis());
		this.mainKey = serviceKey;
		this.token = getIp() + getTime() + getMainKey();
		
	}

	public String getLoginId() {
		return loginId;
	}

	public String getLoginPw() {
		return loginPw;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void setLoginPw(String loginPw) {
		this.loginPw = loginPw;
	}

	public String getUsername() {
		return this.uuid;
	}
	
	public String getPassword() {
		return this.token;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return ip;
	}

	public String getTime() {
		return this.time;
	}

	public String getMainKey() {
		return mainKey;
	}

	public String getUuid() {
		return this.uuid;
	}
}
