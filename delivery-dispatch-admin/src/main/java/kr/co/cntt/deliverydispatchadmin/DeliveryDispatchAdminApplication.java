package kr.co.cntt.deliverydispatchadmin;

//import kr.co.cntt.core.concurrent.ConcurrentConfig;
import kr.co.cntt.core.config.RedisConfig;
import kr.co.cntt.deliverydispatchadmin.config.CacheConfig;
import kr.co.cntt.deliverydispatchadmin.config.DeliveryDispatchAdminWebConfig;
import kr.co.cntt.rest.config.RestConfig;
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
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages={"kr.co.cntt.deliverydispatchadmin", "kr.co.cntt.core"})
//@Import({DeliveryDispatchAdminWebConfig.class, ConcurrentConfig.class, CacheConfig.class, RestConfig.class, RedisConfig.class})
@Import({DeliveryDispatchAdminWebConfig.class, CacheConfig.class, RestConfig.class, RedisConfig.class})
@EnableCaching
@EnableScheduling
public class DeliveryDispatchAdminApplication extends SpringBootServletInitializer {
	private static ApplicationContext context;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.profiles("admin").sources(DeliveryDispatchAdminApplication.class);
	}

	public static void main(String[] args) throws Exception {
		context = SpringApplication.run(DeliveryDispatchAdminApplication.class, args);
	}

	public static Object getBean(String beanName) { return context.getBean(beanName); }

}
