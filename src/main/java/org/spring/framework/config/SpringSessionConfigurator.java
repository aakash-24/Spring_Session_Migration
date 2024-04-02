package org.spring.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.session.MultiSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.mongo.SpringMongoSessionConfig;
import org.springframework.session.data.redis.SpringRedisSessionConfig;
import org.springframework.session.jdbc.SpringRdbmsSessionConfig;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.transaction.support.TransactionOperations;

@Slf4j
@Configuration
public class SpringSessionConfigurator extends SpringHttpSessionConfiguration {

    @Value("${spring.session.enable.redis.bean:true}")
    private boolean enable_redis_bean;

    @Value("${spring.session.enable.mongo.bean:true}")
    private boolean enable_mongo_bean;

    @Value("${spring.session.enable.rdbms.bean:true}")
    private boolean enable_jdbc_bean;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private TransactionOperations transactionOperations;

    @Bean
    public <S extends Session> SessionRepositoryFilter
            <? extends Session> springSessionRepositoryFilter
            (SessionRepository<S> sessionRepository) {

        SessionRepositoryFilter sessionRepositoryFilter =
                new SessionRepositoryFilter(sessionRepository());
        return sessionRepositoryFilter;
    }

    @Bean
    public SessionRepository sessionRepository() {
        MultiSessionRepository multiSessionRepository = new MultiSessionRepository();
        if(enable_mongo_bean)
            multiSessionRepository.setSpringMongoSessionConfigs(springMongoSessionConfig());
        if(enable_redis_bean)
            multiSessionRepository.setRedisSessionConfig(springRedisSessionConfig());
        if (enable_jdbc_bean)
            multiSessionRepository.setJdbcSessionConfig(springRdbmsSessionConfig());
        log.info("Initialized Multi-Session Repository");
        return multiSessionRepository;
    }

    @Bean
    @ConditionalOnProperty(havingValue= "true",value = "spring.session.enable.redis.bean")
    public SpringRedisSessionConfig springRedisSessionConfig() {
        return new SpringRedisSessionConfig();
    }

    @Bean
    @ConditionalOnProperty(havingValue = "true",value= "spring.session.enable.mongo.bean")
    public SpringMongoSessionConfig springMongoSessionConfig() {
        return new SpringMongoSessionConfig(mongoOperations);
    }

    @Bean
    @ConditionalOnProperty(havingValue= "true", value= "spring.session.enable.rdbms.bean")
    public SpringRdbmsSessionConfig springRdbmsSessionConfig(){
        return new SpringRdbmsSessionConfig(jdbcOperations,transactionOperations);
    }
}
