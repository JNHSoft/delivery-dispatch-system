package kr.co.cntt.deliverydispatchadmin.config;

import kr.co.cntt.core.service.EhCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class CacheConfig {
	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cacheFactory = new EhCacheManagerFactoryBean();
		cacheFactory.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cacheFactory.setShared(true);	// cache manager sharing config
		cacheFactory.setCacheManagerName("cacheManager");
		return cacheFactory;
	}
	
	@Bean
	public EhCacheService ehCacheService(net.sf.ehcache.CacheManager cacheManager) {
		return new EhCacheService(cacheManager);
	}
}
