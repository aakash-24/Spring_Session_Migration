package org.springframework.session.data.redis;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.spring.framework.SpringDataStore;
import org.spring.framework.SpringMongoSessionConfig;
import org.spring.framework.SpringRedisSessionConfig;
import org.spring.framework.SpringSessionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.SpringMongoSessionConverterUtil;

@Slf4j
public class MultiSessionRepository implements SessionRepository {

    @Value("${spring.primary_storage}")
    private String primarystorage;

    @Value("${spring.secondary_storageIsEnabled}")
    private boolean secondarystorage;

    private SpringMongoSessionConfig springMongoSessionConfig;

    private SpringRedisSessionConfig springRedisSessionConfig;

    public SessionRepository getSessionRepositoryBean() {
        if (StringUtils.isNotEmpty(primarystorage)
                && primarystorage.equals(SpringDataStore.MONGO.name())) {
            return springMongoSessionConfig.getMongoOperationsSessionRepository();
        } else {
            return springRedisSessionConfig.getRedisOperationsSessionRepository();
        }
    }

    @Override
    public Session createSession() {
        return getSessionRepositoryBean().createSession();
    }

    @Override
    public void save(Session session) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        sessionRepository.save(session);
        saveSessionAsSecondary(session);
    }


    private void saveSessionAsSecondary(Session session) {
        if(secondarystorage &&  getSessionRepositoryBean() instanceof MongoIndexedSessionRepository ) {
            SpringSessionData springSessionData = SpringMongoSessionConverterUtil.convertToSessionData(session);
            springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
        }else if(secondarystorage &&  getSessionRepositoryBean() instanceof RedisIndexedSessionRepository){
            SpringSessionData springSessionData = SpringRedisSessionConverterUtil.convertToSessionData(session);
            springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
        }
    }

    @Override
    public Session findById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        return sessionRepository.findById(id);
        
    }

    @Override
    public void deleteById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        sessionRepository.deleteById(id);
        deleteSessionsFromSecondary(id);
    }

    private void deleteSessionsFromSecondary(String sessionId) {
        if(secondarystorage && SpringDataStore.MONGO.name().equals(primarystorage)){
            springRedisSessionConfig.getSpringRedisOperationsSessionRepository().deleteById(sessionId);
        }
        else if(secondarystorage && SpringDataStore.REDIS.name().equals(primarystorage)){
            springMongoSessionConfig.getSpringMongoOperationsSessionRepository().deleteById(sessionId);
        }
    }

    public void setSpringMongoSessionConfigs(SpringMongoSessionConfig springMongoSessionConfig) {
        this.springMongoSessionConfig = springMongoSessionConfig;
    }

    public void setRedisSessionConfig(SpringRedisSessionConfig springRedisSessionConfig) {
        this.springRedisSessionConfig = springRedisSessionConfig;
    }
}
