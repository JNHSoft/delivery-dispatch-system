package kr.co.deliverydispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import kr.co.deliverydispatch.config.DeliveryDispatchWebConfig;
import kr.co.deliverydispatch.config.CacheConfig;
import kr.co.cntt.core.concurrent.ConcurrentConfig;
import kr.co.cntt.rest.config.RestConfig;

/**
 * burgerking spring boot web application
 * spring boot의 embeded was를 사용하지 않고 external was로 배포를 하기 위해서는 반드시
 * SpringBootServletInitializer 객체를 상속받아 configure()메소드를 override해야한다.
 * context를 분리하지 않고 단일 application context로 프로젝트에 적용되어 있다. 컨텍스트 분리 시 contextaware 구현체 사용에 주의
 * @author su
 *
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages={"kr.co.deliverydispatch", "kr.co.cntt.core"})
@Import({DeliveryDispatchWebConfig.class, ConcurrentConfig.class, CacheConfig.class, RestConfig.class})
@EnableCaching
public class DeliveryDispatchWeb extends SpringBootServletInitializer {
    private static ApplicationContext context;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.profiles("web").sources(DeliveryDispatchWeb.class);
    }

    public static void main(String[] args) throws Exception {
        context = SpringApplication.run(DeliveryDispatchWeb.class, args);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
}
