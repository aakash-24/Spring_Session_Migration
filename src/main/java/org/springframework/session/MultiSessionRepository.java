package org.springframework.session;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.data.mongo.SpringMongoSessionConfig;
import org.springframework.session.jdbc.SpringRdbmsSessionConfig;
import org.springframework.session.data.redis.SpringRedisSessionConfig;
import org.spring.framework.data.SpringDataStore;
import org.spring.framework.data.SpringSessionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.SpringMongoSessionConverterUtil;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.SpringRedisSessionConverterUtil;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.SpringJdbcSessionConverterUtil;

@Slf4j
public class MultiSessionRepository implements SessionRepository {

    @Value("${spring.session.primary_storage}")
    private String primarystorage;

    @Value("${spring.session.secondary_storageIsEnabled}")
    private boolean secondarystorageIsEnabled;

    @Value("${spring.session.secondary_storage}")
    private String secondarystorage;

    private SpringMongoSessionConfig springMongoSessionConfig;

    private SpringRedisSessionConfig springRedisSessionConfig;

    private SpringRdbmsSessionConfig springRdbmsSessionConfig;

    public SessionRepository getSessionRepositoryBean() {
        if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.MONGO.name())) {
            log.info("Primary Storage is MONGO");
            return springMongoSessionConfig.getMongoOperationsSessionRepository();
        }else if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.REDIS.name())){
            log.info("Primary Storage is REDIS");
            return springRedisSessionConfig.getRedisOperationsSessionRepository();
        }else if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.JDBC.name())){
            log.info("Primary Storage is JDBC");
            return springRdbmsSessionConfig.getJdbcIndexedSessionRepository();
        }
        log.error("No Primary Storage Configured");
        return null;
    }

    @Override
    public Session createSession() {
        log.info("Going to create session");
        return getSessionRepositoryBean().createSession();
    }

    @Override
    public void save(Session session) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to save session");
        sessionRepository.save(session);
        saveSessionAsSecondary(session);
    }


    private void saveSessionAsSecondary(Session session) {
        if(secondarystorageIsEnabled &&  getSessionRepositoryBean() instanceof MongoIndexedSessionRepository ) {
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.JDBC.name())){
                log.info("Secondary Storage Storage is JDBC");
                SpringSessionData springSessionData = SpringMongoSessionConverterUtil.convertToSessionData(session);
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.REDIS.name())){
                log.info("Secondary Storage Storage is REDIS");
                SpringSessionData springSessionData = SpringMongoSessionConverterUtil.convertToSessionData(session);
                springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }else if(secondarystorageIsEnabled &&  getSessionRepositoryBean() instanceof RedisIndexedSessionRepository){
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.MONGO.name())){
                log.info("Secondary Storage Storage is MONGO");
                SpringSessionData springSessionData = SpringRedisSessionConverterUtil.convertToSessionData(session);
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.JDBC.name())){
                log.info("Secondary Storage Storage is JDBC");
                SpringSessionData springSessionData = SpringRedisSessionConverterUtil.convertToSessionData(session);
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }else if (secondarystorageIsEnabled && getSessionRepositoryBean() instanceof JdbcIndexedSessionRepository){
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.REDIS.name())){
                log.info("Secondary Storage Storage is REDIS");
                SpringSessionData springSessionData = SpringJdbcSessionConverterUtil.convertToSessionData(session);
                springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.MONGO.name())){
                log.info("Secondary Storage Storage is MONGO");
                SpringSessionData springSessionData = SpringJdbcSessionConverterUtil.convertToSessionData(session);
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }
    }

    @Override
    public Session findById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to find session");
        return sessionRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to delete session");
        sessionRepository.deleteById(id);
        deleteSessionsFromSecondary(id);
    }

    private void deleteSessionsFromSecondary(String sessionId) {
        if(secondarystorageIsEnabled && (secondarystorage).equals(SpringDataStore.MONGO.name())){
            log.info("Going to delete MONGO as secondary session");
            springMongoSessionConfig.getSpringMongoOperationsSessionRepository().deleteById(sessionId);
        }
        else if(secondarystorageIsEnabled && (secondarystorage).equals(SpringDataStore.REDIS.name())){
            log.info("Going to delete REDIS as secondary session");
            springRedisSessionConfig.getRedisOperationsSessionRepository().deleteById(sessionId);
        }
        else if(secondarystorageIsEnabled && (secondarystorage).equals(SpringDataStore.JDBC.name())){
            log.info("Going to delete JDBC as secondary session");
            springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().deleteById(sessionId);
        }
    }

    public void setSpringMongoSessionConfigs(SpringMongoSessionConfig springMongoSessionConfig) {
        this.springMongoSessionConfig = springMongoSessionConfig;
    }

    public void setRedisSessionConfig(SpringRedisSessionConfig springRedisSessionConfig) {
        this.springRedisSessionConfig = springRedisSessionConfig;
    }

    public void setJdbcSessionConfig(SpringRdbmsSessionConfig springRdbmsSessionConfig){
        this.springRdbmsSessionConfig = springRdbmsSessionConfig;
    }
}
