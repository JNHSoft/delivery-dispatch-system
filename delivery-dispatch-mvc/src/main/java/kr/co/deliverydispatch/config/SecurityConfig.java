package kr.co.deliverydispatch.config;

import kr.co.deliverydispatch.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * <p>kr.co.deliverydispatch.config
 * <p>SecurityConfig.java
 * <p>spring boot security java config
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 프록시 지원
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * <p>로그인 체크 및 권한 설정
     */
    private CustomAuthenticationProvider customAuthenticationProvider;
    /**
     * <p>성공 제어
     */
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    /**
     * <p>실패 제어
     */
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfig(){}
    /**
     * @param customAuthenticationProvider 로그인 체크 및 권한 설정
     * @param customAuthenticationSuccessHandler 성공 제어
     * @param customAuthenticationFailureHandler 실패 제어
     */
    @Autowired
    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider
            , CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler
            , CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }
    /**
     * <p>createStorePerformanceHistoryFilter
     * <p>관리자 수행 이력 로그 필터
     * @return 필터
     * @throws Exception
     */
    @Bean("storePerformanceHistoryFilter")
    public StorePerformanceHistoryFilter createStorePerformanceHistoryFilter() throws Exception {
        return new StorePerformanceHistoryFilter();
    }
    /**
     * <p>createCustomLogoutHandler
     * <p>로그아웃 핸들러
     * @return 핸들러
     */
    @Bean("customLogoutHandler")
    public LogoutHandler createCustomLogoutHandler() throws Exception {
        return new CustomLogoutHandler();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/files/**");
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()	// 1회성 인증키 csrf토큰 미사용
                .authorizeRequests()
                // 권한 상관 없이 접근 허용
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/tracker").permitAll()    // 트래커 페이지
                // 슈퍼, 개발자만 허용
                .antMatchers("/admin/list/**").access("hasRole('SUPER') or hasRole('GOD')")
                .antMatchers("/admin/delete/**").access("hasRole('SUPER') or hasRole('GOD')")
                // STORE 권한 접근 허용
                .antMatchers("/**").access("hasRole('STORE')")
                .and()
                .formLogin()
                .loginPage("/login").permitAll()    				// 로그인 페이지
                .usernameParameter("storeId")						// 로그인 아이디
                .passwordParameter("storePassword")					// 로그인 패스워드
                .loginProcessingUrl("/loginProcess")		    	// 로그인 처리 URL
                //.successForwardUrl("/store/loginSuccess")			// 로그인 후 URL
                .successHandler(customAuthenticationSuccessHandler)	// 인증 성공 핸들러
                .failureHandler(customAuthenticationFailureHandler) // 인증 실패 핸들러
                .and()
                .logout()
                .addLogoutHandler(createCustomLogoutHandler())
                .logoutRequestMatcher(new AntPathRequestMatcher("/logoutProcess"))	// 로그아웃 처리 URL
                .logoutSuccessUrl("/logoutSuccess").permitAll()						// 로그아웃 후 URL
                .clearAuthentication(true)			// 초기화
                .deleteCookies("JSESSIONID")		// 쿠키 삭제
                .invalidateHttpSession(true)		// 세션 삭제
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error/403")	// 403(권한없음) URL
                .and()
                .addFilterAfter(createStorePerformanceHistoryFilter(), BasicAuthenticationFilter.class);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 로그인 정보 확인 및 권한 설정
        auth.authenticationProvider(customAuthenticationProvider);
    }
}

