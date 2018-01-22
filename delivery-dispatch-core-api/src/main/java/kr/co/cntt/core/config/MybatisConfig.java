package kr.co.cntt.core.config;

import lombok.Data;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;

/**
 * <p>kr.co.cntt.core.config
 * <p>MybatisConfig.java
 * <p>mybatis-config.xml > java config
 * @author JIN
 */
@Data
@Configuration
public class MybatisConfig {
    /**
     * <p>데이타 소스
     * @author JIN
     */
    private DataSource dataSource;
    /**
     * <p>마이바티스 ID
     * @author JIN
     */
    @Value("${spring.mybatisEnvironmentId}")
    private String mybatisEnvironmentId;
    /**
     * <p>getConfiguration
     * <p>마이바티스
     * <p>JAVA CONFIG
     * @return org.apache.ibatis.session.Configuration
     * @author JIN
     */
    public org.apache.ibatis.session.Configuration getConfiguration() {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment enviroment = new Environment(mybatisEnvironmentId, transactionFactory, dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(enviroment);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(false);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseColumnLabel(true);
        configuration.setUseGeneratedKeys(false);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.WARNING);
        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
        configuration.setDefaultStatementTimeout(25);
        configuration.setDefaultFetchSize(100);
        configuration.setSafeRowBoundsEnabled(false);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLocalCacheScope(LocalCacheScope.SESSION);
        configuration.setJdbcTypeForNull(JdbcType.OTHER);
        configuration.setLazyLoadTriggerMethods(new HashSet<String>(Arrays.asList(new String[]{"equals", "clone", "hashCode", "toString"})));
        return configuration;
    }
}
