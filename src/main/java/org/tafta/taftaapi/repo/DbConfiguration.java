package org.tafta.taftaapi.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tafta.taftaapi.config.PropConfiguration;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0908h
 */
@Slf4j
@Configuration
public class DbConfiguration {
    @Autowired
    PropConfiguration config;

    private PoolProperties poolProperties() {
        PoolProperties pool = new PoolProperties();

        String dbUrl = "jdbc:postgresql://" + config.getDbHost() + ":" + config.getDbPort() + "/" + config.getDatabase();

        pool.setUrl(dbUrl);
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUsername(config.getDbUsername());
        pool.setPassword(config.getDbPassword());
        pool.setJmxEnabled(true);
        pool.setTestWhileIdle(false);
        pool.setTestOnBorrow(true);
        pool.setValidationQuery("SELECT 1");
        pool.setInitSQL("SELECT 1");
        pool.setTestOnReturn(false);
        pool.setValidationInterval(60000); // setting it to 60 sec instead of 30 secs
        pool.setTimeBetweenEvictionRunsMillis(30000);
        pool.setMaxActive(100);
        pool.setInitialSize(10);
        pool.setMaxWait(10000);
        pool.setRemoveAbandonedTimeout(60);
        pool.setMinEvictableIdleTimeMillis(30000);
        pool.setMinIdle(10);
        pool.setLogAbandoned(true);
        pool.setRemoveAbandoned(true);
        pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");

        log.info("Db Connection created " + pool.getInitialSize());

        return pool;
    }

    @Bean
    public DataSource dataSource(){
        return new DataSource(poolProperties());
    }
}
