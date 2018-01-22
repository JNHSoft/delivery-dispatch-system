package kr.co.cntt.core.util;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ShaEncoder implements PasswordEncoder{

	private ShaPasswordEncoder shaPasswordEncoder;
	private Object salt = null;
	
	public ShaEncoder() {
		this.shaPasswordEncoder = new ShaPasswordEncoder();
	}
	
	public ShaEncoder(int sha) {
		this.shaPasswordEncoder = new ShaPasswordEncoder(sha);
	}
	
	public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
		shaPasswordEncoder.setEncodeHashAsBase64(encodeHashAsBase64);
	}
	
	public void setSalt(Object salt) {
		this.salt = salt;
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		return shaPasswordEncoder.encodePassword(rawPassword.toString(), this.salt);
	}
	
	public String md5Sha256Encode(String rawPassword) {
		MD5Encoder md5 = new MD5Encoder();
		String encodedPassword = md5.encode(rawPassword);
		return encodedPassword; 
		//return this.encode(encodedPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return shaPasswordEncoder.isPasswordValid(encodedPassword, rawPassword.toString(), this.salt);
	}
}
