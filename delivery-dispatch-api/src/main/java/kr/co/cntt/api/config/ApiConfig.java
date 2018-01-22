package kr.co.cntt.api.config;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;

import kr.co.cntt.core.config.DeliveryDispatchDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;


import kr.co.cntt.rest.custom.mapper.RestObjectMapper;

/**
 * 어플리케이션 configuration
 * 
 * @author su
 *
 */
@Configuration
@Import({DeliveryDispatchDatabaseConfig.class})
public class ApiConfig {
	/**
	 * web mvc configuration
	 * @author su
	 */
	@Configuration
	static class WebMvcConfig extends WebMvcConfigurerAdapter {
		@Value("${spring.mvc.locale}")
		Locale locale = null;
		@Value("${spring.messages.basename}")
		String messagesBasename = null;
		@Value("${spring.messages.encoding}")
		String messagesEncoding = null;
		@Value("${spring.messages.cache-seconds}")
		int messagesCacheSeconds;

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
		}

		// 다국어
		@Bean
		public LocaleResolver localeResolver() {
			SessionLocaleResolver slr = new SessionLocaleResolver();
			slr.setDefaultLocale(locale);
			return slr;
		}

		@Bean
		public LocaleChangeInterceptor localeChangeInterceptor() {
			LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
			lci.setParamName("lang");
			return lci;
		}

		// message source
		@Bean
		public ReloadableResourceBundleMessageSource messageSource() {
			ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
			messageSource.setBasenames(messagesBasename.split(","));
			messageSource.setDefaultEncoding(messagesEncoding);
			messageSource.setCacheSeconds(messagesCacheSeconds);
			return messageSource;
		}

		@Bean
		public MessageSourceAccessor getMessageSourceAccessor() {
			ReloadableResourceBundleMessageSource m = messageSource();
			return new MessageSourceAccessor(m);
		}

		@Bean("objectMapper")
		@Profile("api")
		@Primary
		public RestObjectMapper getObjectMapperApi() {
			RestObjectMapper objectMapper = new RestObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper;
		}

		@Bean
		public MappingJackson2HttpMessageConverter getJackson(@Qualifier("objectMapper") RestObjectMapper objectMapper) {
			MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
			jsonConverter.setObjectMapper(objectMapper);
			jsonConverter.setDefaultCharset(Charset.forName("UTF-8"));
			jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json")));
			return jsonConverter;
		}
		
		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			// registry.addResourceHandler("/resources/**").addResourceLocations("classpath:static/");
			// // classpath 하위
			registry.addResourceHandler("/resources/**").addResourceLocations("/resources/"); // webapp
																								// 하위
		}
	}
	/*
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public FilterRegistrationBean getFilterRegistrationBean() throws ServletException {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter());
		registrationBean.addUrlPatterns("/BkrApp");
		return registrationBean;
	}
	*/
}
