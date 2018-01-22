package kr.co.cntt.core.config;

import com.github.pagehelper.PageInterceptor;
import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * datasource configuration
 * 1. datasource config
 * 2. transaction manager config
 * @author Merlin
 *
 */
@Configuration
@MapperScan(basePackages = "kr.co.cntt.core", annotationClass = DeliveryDispatchMapper.class, sqlSessionFactoryRef = " deliveryDispatchSqlSessionFactory")
@EnableTransactionManagement
public class DeliveryDispatchDatabaseConfig {

    @Bean(name = " deliveryDispatchDatasource")
    @ConfigurationProperties(prefix="datasource.deliveryDispatch")
    @Profile("local")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "deliveryDispatchDatasource")
    @Profile({"dev", "oper"})
    public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/deliveryDispatch");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(true);
        bean.afterPropertiesSet();
        return (DataSource)bean.getObject();
    }

    @Bean(name = "deliveryDispatchPageInterceptor")
    public PageInterceptor deliveryDispatchPageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    @Bean(name = "deliveryDispatchSqlSessionFactory")
    public SqlSessionFactory deliveryDispatchSqlSessionFactory(
            @Qualifier("deliveryDispatchDatasource") DataSource deliveryDispatchDatasource, ApplicationContext context, @Autowired MybatisConfig mybatisConfig)
            throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(deliveryDispatchDatasource);

        sessionFactoryBean.setMapperLocations(context.getResources("classpath*:/mybatis/config/**/*.xml"));

        mybatisConfig.setDataSource(deliveryDispatchDatasource);
        sessionFactoryBean.setConfiguration(mybatisConfig.getConfiguration());
        sessionFactoryBean.setPlugins(new Interceptor[] {deliveryDispatchPageInterceptor()});
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "deliveryDispatchTransactionManager")
    public PlatformTransactionManager deliveryDispatchTransactionManager(
            @Qualifier("deliveryDispatchDatasource") DataSource deliveryDispatchDatasource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(deliveryDispatchDatasource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }
}
