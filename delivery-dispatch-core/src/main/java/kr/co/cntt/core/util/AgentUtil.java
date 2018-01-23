package kr.co.cntt.core.util;

import javax.servlet.http.HttpServletRequest;

public class AgentUtil {

	private static final String SERVER_NAME = "server.name";

	/**
	 * find ip address
	 * 
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		try {
			String ip = request.getHeader("X-Forwarded-For");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			return ip;
		} catch (Exception e) {
			return "";
		}
	}

	public static String getOs(HttpServletRequest request) {
		try {
			String os;
			String userAgent = request.getHeader("Rider-Agent");
			if (userAgent.toLowerCase().indexOf("android") >= 0) {
				os = "Android";
			} else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
				os = "iOS";
			} else {
				os = null;
			}
			return os;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * host server name
	 * 
	 * @param request
	 * @return
	 */
	public static String getChannel(HttpServletRequest request) {
		return System.getProperty(SERVER_NAME);
	}
}
