package kr.co.cntt.deliverydispatchadmin.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>kr.co.burgerking.interceptor
 * <p>LoginInterceptor.java
 * <p>회원
 * <p>로그인, 회원, 비회원 관련 인터셉터
 * @author JIN
 */
public class LoginInterceptor implements HandlerInterceptor {
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		/*
		// 현재 URI를 request에서 취득
		String requestUri = request.getRequestURI();
		// 로그인 SESSION GET
		HttpSession httpSession = request.getSession();
		Member member = (Member)httpSession.getAttribute(SessionEnum.LOGIN_SESSION.getSessionKey());
		if ("/member/login".equals(requestUri) && member != null) {
		// 로그인 후 로그인 페이지 진입 방지 == 메인 페이지로 리다이렉트 처리
			response.sendRedirect("/");
		} else if ((!requestUri.endsWith("/memberWithdrawSuccess") && !requestUri.endsWith("/nonMemberOrderLookupNumber") 
				&& !requestUri.endsWith("/nonMemberOrderLookupPhone") && !requestUri.endsWith("/nonMemberOrderDetail"))
				&& ((requestUri.startsWith("/mypage") || "/order/memberOrder".equals(requestUri) || "/order/payment".equals(requestUri)) && member == null)) {
		// 로그인 필수 페이지 검사 후 리다이렉트
			response.sendRedirect("/member/login");
			return false;
		} else if (requestUri.startsWith("/mypage") && member != null && "nonMember".equals(member.getLoginFlag())) {
		// 비회원 접근 제한
			response.sendRedirect("/");
			return false;
		}
		*/
		return true;
	}
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}
