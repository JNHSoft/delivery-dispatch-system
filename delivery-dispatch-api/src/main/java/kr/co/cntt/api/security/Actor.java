package kr.co.cntt.api.security;

import java.util.UUID;

public class Actor {

	private String uuid;
	private String ip;
	private String time;
	private String mainKey;
	private String token;
	
	public Actor(String ip, String serviceKey) {
		this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
		this.ip = ip;
		this.time = String.valueOf(System.currentTimeMillis());
		this.mainKey = serviceKey;
		this.token = getIp() + getTime() + getMainKey();
		
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
