package kr.co.deliverydispatch.config;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kr.co.cntt.core.config.DeliveryDispatchDatabaseConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.deliverydispatch.controller.ErrorController;
import kr.co.deliverydispatch.interceptor.LoginInterceptor;

@Configuration
@Import({DeliveryDispatchDatabaseConfig.class})
public class DeliveryDispatchWebConfig {
    /**
     * spring web-mvc configuration. This is inner nested static class which
     * extends WebMvcConfigurerAdapter class.
     *
     * @author su
     *
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
        @Value("${spring.file.savePath}")
        String imageFilePath;
        @Value("${cntt.underConstruction.redirectUrl}")
        String redirectUrl;
        @Value("${cntt.underConstruction.ignoreIps:}")
        String[] ignoreIps;
        @Value("${cntt.underConstruction.paramAdminPw}")
        String paramAdminPw;
        @Value("${cntt.underConstruction.active:false}")
        boolean active;
        @Value("${api.upload.path.alarm}")
        String alarmFilePath;

        /**
         * LocaleResolver bean configuration
         *
         * @return LocaleResolver
         */
        @Bean
        public LocaleResolver localeResolver() {
            SessionLocaleResolver slr = new SessionLocaleResolver();
            slr.setDefaultLocale(locale);
            return slr;
        }

        /**
         * LocaleChangeInterceptor bean configuration
         *
         * @return
         */
        @Bean
        public LocaleChangeInterceptor localeChangeInterceptor() {
            LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
            lci.setParamName("lang");
            return lci;
        }

        /**
         * ReloadableResourceBundleMessageSource bean configuration
         *
         * @return
         */
        @Bean
        public ReloadableResourceBundleMessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasenames(messagesBasename.split(","));
            messageSource.setDefaultEncoding(messagesEncoding);
            messageSource.setCacheSeconds(messagesCacheSeconds);
            return messageSource;
        }

        /**
         * MessageSourceAccessor bean configuration
         *
         * @return
         */
        @Bean
        public MessageSourceAccessor getMessageSourceAccessor() {
            ReloadableResourceBundleMessageSource m = messageSource();
            return new MessageSourceAccessor(m);
        }

        @Bean
        public ResourceUrlEncodingFilter ResourceUrlEncodingFilter() {
            return new ResourceUrlEncodingFilter();
        }

        @Bean
        public EmbeddedServletContainerCustomizer containerCustomizer(ConfigurableEmbeddedServletContainer container) {
            return new EmbeddedServletContainerCustomizer() {
                @Override
                public void customize(ConfigurableEmbeddedServletContainer container) {
                    container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, ErrorController.ERROR_401),
                            new ErrorPage(HttpStatus.FORBIDDEN, ErrorController.ERROR_403),
                            new ErrorPage(HttpStatus.NOT_FOUND, ErrorController.ERROR_404),
                            new ErrorPage(Throwable.class, ErrorController.ERROR_DEFAULT));
                }
            };
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation is empty.
         */
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            jsonConverter.setDefaultCharset(Charset.forName("UTF-8"));
            jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json")));
            jsonConverter.setObjectMapper(objectMapper);
            converters.add(jsonConverter);
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation is empty.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // static resource context
            registry.addResourceHandler("/resources/css/**").addResourceLocations("classpath:css/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new PathResourceResolver())
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                    .addTransformer(new CssLinkResourceTransformer());
            // .addResolver(new GzipResourceResolver()).addTransformer(new
            // CssLinkResourceTransformer());
            registry.addResourceHandler("/resources/js/**").addResourceLocations("classpath:js/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                    // .addResolver(new
                    // VersionResourceResolver().addContentVersionStrategy("/**"))
                    .addResolver(new GzipResourceResolver()).addResolver(new PathResourceResolver());
            registry.addResourceHandler("/resources/images/**").addResourceLocations("classpath:images/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new PathResourceResolver());
            // file context
            registry.addResourceHandler("/files/**").addResourceLocations("file:" + imageFilePath)
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new PathResourceResolver());
            registry.addResourceHandler("/alarmFiles/**").addResourceLocations("file:" + alarmFilePath)
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new PathResourceResolver());
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation is empty.
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/main");
            registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
            super.addViewControllers(registry);
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation is empty.
         */
        @Override
        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
            registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/member/login", "/mypage/**", "/order/**");

            //공사중 페이지
         /*
         final UnderConstructionInterceptor underConstructionInterceptor = underConstructionInterceptor();
         registry.addInterceptor(underConstructionInterceptor).addPathPatterns("/**")
               .excludePathPatterns(underConstructionInterceptor.getRedirectUrl() + "/**")
               .excludePathPatterns("/resources/**").excludePathPatterns("/files/**")
               .excludePathPatterns("/error/**").excludePathPatterns("/common/**");
         */
        }
        /*
        @Bean
        public UnderConstructionInterceptor underConstructionInterceptor() {
            return new UnderConstructionInterceptor(redirectUrl, paramAdminPw, active,
                    new HashSet<String>(Arrays.asList(ignoreIps)));
        }
        */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(10);
        }
    }
}