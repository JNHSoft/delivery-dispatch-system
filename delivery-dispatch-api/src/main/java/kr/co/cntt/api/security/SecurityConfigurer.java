package kr.co.cntt.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Profile("api")
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	/*@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}*/

	@Bean
	public CustomAuthentificateService getIPBasedAuthentificateService() {
		return new CustomAuthentificateService();
	}
	@Bean
	public CustomAuthentificateEntryPoint getIPBasedAuthentificateEntryPoint() {
		return new CustomAuthentificateEntryPoint();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public AuthentificationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new AuthentificationTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
		authManagerBuilder.userDetailsService(getIPBasedAuthentificateService()).passwordEncoder(passwordEncoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(getIPBasedAuthentificateEntryPoint()).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
			.antMatchers(
				HttpMethod.OPTIONS
			).permitAll()
			.antMatchers(
				HttpMethod.GET
			).permitAll()
			.antMatchers(
				"/",
				"/**/*.html",
				"/**/*.css",
				"/**/*.js"
//				"/**/*.favicon.ico"
			).permitAll()
			//.antMatchers("/BkrApp/setservicekey.do").permitAll()
			.antMatchers("/API/getToken.do").permitAll()
			.antMatchers("/API/putToken.do").permitAll()
			.antMatchers("/API/versionCheck.do").permitAll()
			.antMatchers("/POS/*").permitAll()
//			.antMatchers("/API/*").permitAll()
			.anyRequest().authenticated();
		
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		http.headers().cacheControl();
		http.headers().frameOptions().disable();
	}
}
