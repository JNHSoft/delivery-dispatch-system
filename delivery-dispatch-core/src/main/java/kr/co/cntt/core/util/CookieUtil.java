package kr.co.cntt.core.util;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;

/**
 * 쿠키 유틸
 *
 * @author brad
 */
public class CookieUtil {

	/**
	 * 쿠키 값 가져오기
	 *
	 * @param request 쿠키가 다뤄질 request
	 * @param name 쿠키 명
	 * @return 쿠키 값
	 */
	public static String get(@NonNull final HttpServletRequest request, final String name) {
		final Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (final Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 쿠키 추가
	 *
	 * @param response 쿠키가 다뤄질 response
	 * @param name 쿠키 명
	 * @param value 쿠키 값
	 * @param maxAgeSeconds 쿠키 만료시간
	 * @param secure HTTPS 여부. {@link HttpServletRequest#isSecure()}로 가져올 수 있음.
	 * @see #set(HttpServletResponse, String, String, int, String, String, boolean)
	 */
	public static void set(@NonNull final HttpServletResponse response, @NonNull final String name, final String value,
			final int maxAgeSeconds, final boolean secure) {
		set(response, name, value, maxAgeSeconds, null, null, secure);
	}

	/**
	 * 쿠키 추가(상세)
	 *
	 * @param response 쿠키가 다뤄질 response
	 * @param name 쿠키 명
	 * @param value 쿠키 값
	 * @param maxAgeSeconds 쿠키 만료시간
	 * @param domain 쿠키 도메인
	 * @param path 쿠키 경로. isEmpty(path) == null ? "/" : path;
	 * @param secure HTTPS 여부. {@link HttpServletRequest#isSecure()}로 가져올 수 있음.
	 * @see #set(HttpServletResponse, String, String, int, boolean)
	 */
	public static void set(@NonNull final HttpServletResponse response, @NonNull final String name, final String value,
			final int maxAgeSeconds, final String domain, final String path, final boolean secure) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeSeconds);
		if (!isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setPath(isEmpty(path) ? "/" : path);
		cookie.setSecure(secure);

		response.addCookie(cookie);
	}

	/**
	 * 쿠키 제거
	 *
	 * @param response 쿠키가 다뤄질 response
	 * @param name 쿠키 명
	 * @param secure HTTPS 여부. {@link HttpServletRequest#isSecure()}로 가져올 수 있음.
	 */
	public static void remove(@NonNull final HttpServletResponse response, final String name, final boolean secure) {
		remove(response, name, null, null, secure);
	}

	/**
	 * 쿠키 제거(상세)
	 *
	 * @param response 쿠키가 다뤄질 response
	 * @param name 쿠키 명
	 * @param domain 쿠키 도메인
	 * @param path 쿠키 경로. isEmpty(path) == null ? "/" : path;
	 * @param secure HTTPS 여부. {@link HttpServletRequest#isSecure()}로 가져올 수 있음.
	 */
	public static void remove(@NonNull final HttpServletResponse response, final String name, final String domain,
			final String path, final boolean secure) {
		set(response, name, null, 0, domain, path, secure);
	}

	/**
	 * 특정 쿠키를 제외한 모든 쿠키 제거
	 *
	 * @param request 쿠키가 다뤄질 request
	 * @param response 쿠키가 다뤄질 response
	 * @param ignoreNames 지우지 않을 쿠키명 목록
	 */
	public static void clear(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
			final String... ignoreNames) {
		clear(request, response, null, null, ignoreNames);
	}

	/**
	 * 특정 쿠키를 제외한 모든 쿠키 제거(상세)
	 *
	 * @param request 쿠키가 다뤄질 request
	 * @param response 쿠키가 다뤄질 response
	 * @param domain 쿠키 도메인
	 * @param path 쿠키 경로. isEmpty(path) == null ? "/" : path;
	 * @param ignoreNames 지우지 않을 쿠키명 목록
	 */
	public static void clear(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
			final String domain, final String path, final String... ignoreNames) {
		final Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return;
		}

		final List<String> whiteList = Arrays.asList(ignoreNames);
		for (final Cookie cookie : cookies) {
			if (whiteList.contains(cookie.getName())) {
				continue;
			}
			remove(response, cookie.getName(), domain, path, request.isSecure());
		}
	}
}
