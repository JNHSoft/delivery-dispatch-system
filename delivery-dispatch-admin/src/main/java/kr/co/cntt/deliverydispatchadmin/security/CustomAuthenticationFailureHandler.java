package kr.co.cntt.deliverydispatchadmin.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * <p>kr.co.burgerking.admin.security
 * <p>CustomAuthenticationFailureHandler.java
 * <p>스프링 시큐리티
 * <p>인증 실패 핸들러
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.AuthenticationFailureHandler#onAuthenticationFailure(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request
            , HttpServletResponse response
            , AuthenticationException exception) throws IOException, ServletException {
        String exceptionType = "";
        if (exception instanceof LockedException) {
            // LockedException
            // 비밀번호 실패 카운트가 3이상 일때
            exceptionType = "LK";
        } else if (exception instanceof DisabledException) {
            // DisabledException
            // 비밀번호 변경한지 30일 이상 경과 시
            exceptionType = "DB";
        } else {
            // BadCredentialsException
            // 계정 정보 틀린 경우
            exceptionType = "BC";
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter out = response.getWriter();
        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("result", "F");
            map.put("et", exceptionType);
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
     * <p>CustomAuthenticationFailureHandler.java
     * <p>에러 컨트롤 내부 class
     * @author JIN
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
