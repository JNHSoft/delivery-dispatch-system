package kr.co.cntt.api;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import kr.co.cntt.api.security.SecurityConfigurer;
import kr.co.cntt.api.config.ApiConfig;
import kr.co.cntt.core.concurrent.ConcurrentConfig;
import kr.co.cntt.rest.config.RestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("api")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages={"kr.co.cntt.api", "kr.co.cntt.core"})
@EnableCaching
@Import({ApiConfig.class, ConcurrentConfig.class, SecurityConfigurer.class, RestConfig.class})
//@PropertySource("classpath:servicekey.properties")
@EnableAspectJAutoProxy
public class ApiApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApiApplication.class);
    }
    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(ApiApplication.class, args);
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.debug("Bean load : {}", beanName);
        }
    }

    @Bean
    public SpringApplicationContext springApplicationContext() {
        return new SpringApplicationContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public MonitorAspect monitorAspect() {
        return new MonitorAspect();
    }
    @Bean
    @ConditionalOnMissingBean
    public CnttExceptionAdvice exceptionAdvice() {
        return new CnttExceptionAdvice();
    }
}