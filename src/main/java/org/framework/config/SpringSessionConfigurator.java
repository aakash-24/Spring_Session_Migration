package org.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.MultiSessionRepository;
import org.springframework.session.data.mongo.SpringMongoSessionConfig;
import org.springframework.session.data.redis.SpringRedisSessionConfig;
import org.springframework.session.jdbc.SpringRdbmsSessionConfig;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.transaction.support.TransactionOperations;

/**
 * SpringSessionConfigurator configures the Spring session management system based on the configured storage options.
 * It provides beans for session repositories and filters, and initializes session configurations for MongoDB, Redis, and RDBMS.
 * @author Shishir Pandey
 */
@Slf4j
@Configuration
public class SpringSessionConfigurator extends SpringHttpSessionConfiguration {

    @Value("${spring.session.enable.redis.bean:true}")
    private boolean ENABLE_REDIS_BEAN;

    @Value("${spring.session.enable.mongo.bean:false}")
    private boolean ENABLE_MONGO_BEAN;

    @Value("${spring.session.enable.rdbms.bean:false}")
    private boolean ENABLE_RDBMS_BEAN;

    @Autowired(required = false)
    private MongoOperations mongoOperations;

    @Autowired(required = false)
    private JdbcOperations jdbcOperations;

    @Autowired(required = false)
    private TransactionOperations transactionOperations;

    /**
     * Configures the session repository filter.
     * @param sessionRepository The session repository to be filtered.
     * @param <S> The type of session.
     * @return The configured session repository filter.
     */
    @Bean
    @Override
    public <S extends Session> SessionRepositoryFilter
            <? extends Session> springSessionRepositoryFilter
    (SessionRepository<S> sessionRepository) {

        SessionRepositoryFilter sessionRepositoryFilter =
                new SessionRepositoryFilter(sessionRepository());
        return sessionRepositoryFilter;
    }

    /**
     * Configures the session repository based on enabled storage options.
     * @return The configured session repository.
     */
    @Bean
    public SessionRepository sessionRepository() {
        MultiSessionRepository multiSessionRepository = new MultiSessionRepository();
        if(ENABLE_MONGO_BEAN) {
            log.info("Mongo bean initialized");
            multiSessionRepository.setSpringMongoSessionConfigs(springMongoSessionConfig());
        }
        if(ENABLE_REDIS_BEAN) {
            log.info("Redis bean initialized");
            multiSessionRepository.setRedisSessionConfig(springRedisSessionConfig());
        }
        if (ENABLE_RDBMS_BEAN) {
            log.info("Rdbms bean initialized");
            multiSessionRepository.setRdbmsSessionConfig(springRdbmsSessionConfig());
        }
        log.info("Initialized Multi-Session Repository");
        return multiSessionRepository;
    }

    /**
     * Configures the Spring Redis session.
     * @return The configured SpringRedisSessionConfig bean.
     */
    @Bean
    @ConditionalOnProperty(havingValue= "true",value = "spring.session.enable.redis.bean")
    public SpringRedisSessionConfig springRedisSessionConfig() {
        return new SpringRedisSessionConfig();
    }

    /**
     * Configures the Spring MongoDB session.
     * @return The configured SpringMongoSessionConfig bean.
     */
    @Bean
    @ConditionalOnProperty(havingValue = "true",value= "spring.session.enable.mongo.bean")
    public SpringMongoSessionConfig springMongoSessionConfig() {
        return new SpringMongoSessionConfig(mongoOperations);
    }

    /**
     * Configures the Spring RDBMS session.
     * @return The configured SpringRdbmsSessionConfig bean.
     */
    @Bean
    @ConditionalOnProperty(havingValue= "true", value= "spring.session.enable.rdbms.bean")
    public SpringRdbmsSessionConfig springRdbmsSessionConfig(){
        return new SpringRdbmsSessionConfig(jdbcOperations,transactionOperations);
    }
}

