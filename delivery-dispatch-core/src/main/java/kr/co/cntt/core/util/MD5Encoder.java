package kr.co.cntt.core.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5Encoder implements PasswordEncoder{

	private Md5PasswordEncoder md5PasswordEncoder;
	private Object salt = null;
	
	public MD5Encoder() {
		this.md5PasswordEncoder = new Md5PasswordEncoder();
	}
	
	public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
		md5PasswordEncoder.setEncodeHashAsBase64(encodeHashAsBase64);
	}
	
	public void setSalt(Object salt) {
		this.salt = salt;
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		return md5PasswordEncoder.encodePassword(rawPassword.toString(), this.salt);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return md5PasswordEncoder.isPasswordValid(encodedPassword, rawPassword.toString(), this.salt);
	}
}
