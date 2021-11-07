package kr.co.cntt.api.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter{
	private static final String LOCAL_ORIGIN = "http://localhost:8080";
	private static final String DEV_ORIGIN = "https://dev-store.cntt.co.kr";
	private static final String HK_ORIGIN = "https://ddehk-store.cntt.co.kr";
	private static final String TW_ORIGIN = "https://ddetw-store.cntt.co.kr";

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-auth-token, content-type");
		chain.doFilter(req, res);

		/*
		HttpServletRequest request = (HttpServletRequest) req;

		if (request.getHeader("Origin") == null || (request.getHeader("Origin").contains(LOCAL_ORIGIN) == false &&
				request.getHeader("Origin").contains(DEV_ORIGIN) == false &&
				request.getHeader("Origin").contains(HK_ORIGIN) == false &&
				request.getHeader("Origin").contains(TW_ORIGIN) == false)) {
			return;
		} else {
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "x-auth-token, content-type");
			chain.doFilter(req, res);
		}
		*/
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
