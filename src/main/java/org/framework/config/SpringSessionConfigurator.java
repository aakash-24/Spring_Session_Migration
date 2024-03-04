package org.framework.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Configuration
public class SpringSessionConfigurator extends SpringHttpSessionConfiguration {

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
        multiSessionRepository.setSpringMongoSessionConfigs(springMongoSessionConfig());
        multiSessionRepository.setRedisSessionConfig(springRedisSessionConfig());
        multiSessionRepository.setJdbcSessionConfig(springRdbmsSessionConfig());
        log.info("Initialized Multi-Session Repository");
        return multiSessionRepository;
    }

    @Bean
    public SpringRedisSessionConfig springRedisSessionConfig() {
        return new SpringRedisSessionConfig();
    }

    @Bean
    public SpringMongoSessionConfig springMongoSessionConfig() {
        return new SpringMongoSessionConfig(mongoOperations);
    }

    @Bean
    public SpringRdbmsSessionConfig springRdbmsSessionConfig(){
        return new SpringRdbmsSessionConfig(jdbcOperations,transactionOperations);
    }
}
