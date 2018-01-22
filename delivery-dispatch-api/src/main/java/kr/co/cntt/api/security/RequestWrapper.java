package kr.co.cntt.api.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RequestWrapper extends HttpServletRequestWrapper {
	/** logger */
	private final Logger logger = LoggerFactory.getLogger(RequestWrapper.class);
	/** request body */
	private String body;
	
	/**
	 * consturctor
	 * @param request
	 * @throws Exception
	 */
	public RequestWrapper(HttpServletRequest request) throws Exception {
		super(request);
		body = getBody();
	}
	
	public String getJsonBody() {
		return this.body;
	}
	
	/**
	 * getParameterValues
	 */
	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}
	
	/**
	 * getParameter
	 */
	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}
	
	/**
	 * getHeader
	 */
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null)
			return null;
		return value;

	}

	/**
	 * getInputStream
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInpuitStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream inputStream = new ServletInputStream(){
			public int read() throws IOException {
				return byteArrayInpuitStream.read();
			}
			@Override
			public boolean isFinished() {
				return false;
			}
			@Override
			public boolean isReady() {
				return false;
			}
			@Override
			public void setReadListener(ReadListener readListener) {
				
			}
		};
		return inputStream;
	}
	
	/**
	 * request body 추출
	 * @return
	 * @throws Exception
	 */
	private String getBody() throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = super.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			logger.debug("RequestWrapper getBody Error : {}", ex.getMessage());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					logger.debug("RequestWrapper getBody Error : {}", ex.getMessage());
				}
			}
		}
		return stringBuilder.toString();
	}
	
	/**
	 * XSS 처리
	 * @param value
	 * @return
	 */
	private String cleanXSS(String value) {
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}
	
	public static String extractToken(String reqquest) {
		JsonObject json = new JsonParser().parse(reqquest).getAsJsonObject();
		JsonObject jheader = json.getAsJsonArray("header").get(0).getAsJsonObject();
		return jheader.get("token").toString();
	}
}
