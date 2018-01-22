package kr.co.cntt.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthentificationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private IPBasedAuthentificateService ipBasedAuthentificateService;

	@Autowired
	private TokenManager tokenManager;

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		RequestWrapper request;
		String requestUri= servletRequest.getRequestURI();
		log.debug("app api request uri : {}", requestUri);
		if (requestUri.startsWith("/BkrApp") && !(requestUri.contains("setservicekey.do") || requestUri.contains("gettoken.do"))) {
			try {
				request = new RequestWrapper(servletRequest);
				String authToken = extractToken(request.getJsonBody());
				String username = tokenManager.getUsernameFromToken(authToken);
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					ActorDetails actorDetails = this.ipBasedAuthentificateService.loadUserByUsername(username);
					if (tokenManager.validateToken(authToken, actorDetails)) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								actorDetails, null, actorDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource(). buildDetails(request));
						logger.info("authenticated device " + username + ", setting security context");
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
				// TODO : filter chain stop 시점을 찾아야한다. 오류인 경우 어떻게 할지.. exception 공통구현 필요
				chain.doFilter(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			chain.doFilter(servletRequest, response);
		}
	}

	public static String extractToken(String request) {
		if (request == null || "".equals(request)) {
			return request;
		}
		log.debug("request : {}", request);
		JsonObject json = new JsonParser().parse(request).getAsJsonObject();
		JsonObject jheader = json.getAsJsonArray("header").get(0).getAsJsonObject();
		return jheader.get("token").getAsString();
	}
}
