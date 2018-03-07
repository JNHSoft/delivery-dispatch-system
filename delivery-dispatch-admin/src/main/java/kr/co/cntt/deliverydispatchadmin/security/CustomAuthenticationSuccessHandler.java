package kr.co.cntt.deliverydispatchadmin.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cntt.deliverydispatchadmin.enums.SecurityEnum;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * <p>kr.co.burgerking.admin.security
 * <p>CustomAuthenticationSuccessHandler.java
 * <p>스프링 시큐리티
 * <p>인증 성공 핸들러
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
            , HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter out = response.getWriter();
        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("result", "S");
            SecurityUser securityUser = (SecurityUser)authentication.getDetails();
            if (SecurityEnum.SUPER.getValue().equals(securityUser.getAuthLevel())) {
                map.put("result", "AA");
            }
            out.print(new Gson().toJson(map).toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ExceptionMessage error = new ExceptionMessage(ErrorCodeEnum.S0001.name(), "시스템 오류");
            out.print(new Gson().toJson(error, ExceptionMessage.class).toString());
        } finally {
            out.flush();
            out.close();
        }
    }
    /**
     * <p>kr.co.burgerking.admin.security
     * <p>CustomAuthenticationSuccessHandler.java
     * <p>에러 컨트롤 내부 class
     */
    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    class ExceptionMessage {
        private final String errorCode;
        private final String errorMessage;
        private String redirectUrl;
    }
}
