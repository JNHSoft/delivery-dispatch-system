package kr.co.cntt.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.Resource;

/**
 * <p>kr.co.cntt.core.util
 * <p>StreamUtil.java
 * <p>Stream 유틸
 * @author JIN
 */
public class StreamUtil {
	/**
	 * <p>getStreamReadResult
	 * <p>스트림 리드 결과 문자열 리턴
	 * @param resourcePath 리소스 경로
	 * @return 읽은 결과 
	 * @throws Exception
	 * @author JIN
	 */
	public static String getStreamReadResult(String resourcePath) throws Exception {
		String returnValue = "";
		@SuppressWarnings("resource")		
		StaticApplicationContext context = new StaticApplicationContext();
		Resource resource = context.getResource(resourcePath); 
		context.close();
		if (resource.isReadable()) {
			InputStream inputStream = resource.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder resultStringBuilder = new StringBuilder();
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				resultStringBuilder.append(readLine);
			}
			// 자원 반환 
			bufferedReader.close();
			if (inputStream != null) inputStream.close();
			returnValue = resultStringBuilder.toString();
		}
		return returnValue;
	}
}
