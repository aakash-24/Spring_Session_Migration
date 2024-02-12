package org.spring.framework.data.redis;

import lombok.Setter;
import org.spring.framework.session.MongoSessionConfig;
import org.spring.framework.session.RedisSessionConfig;
import org.spring.framework.session.SessionData;
import org.spring.framework.session.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.spring.framework.session.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.Session;
import org.springframework.session.data.mongo.MongoOperationsSessionRepository;
import org.springframework.session.data.mongo.MongoSession;

/**
 * Description: This class is to manage session repositories configured to manage session Date:
 * 08/06/23-05:40 pm
 *
 * @author aakashjain
 * @since
 */
@Slf4j
public class SessionRepository implements org.springframework.session.SessionRepository {

    @Value("${Session.cookie.max-age.policyEnabled: true}")
    private boolean sessionPolicyEnabled;

    @Setter
    private MongoSessionConfig MongoSessionConfig;

    @Setter
    private RedisSessionConfig RedisSessionConfig;


    /*
    This can be removed once we start using single data source for session
    */
    public org.springframework.session.SessionRepository getSessionRepositoryBean() {
        SessionStore storageType = SessionUtil.getPrimaryStorage();
        if (SessionStore.MONGO.equals(storageType)) {
            if (sessionPolicyEnabled) {
                return MongoSessionConfig.getMongoSessionRepository();
            }
            return MongoSessionConfig.getMongoOperationsSessionRepository();
        } else {
            if (sessionPolicyEnabled) {
                return RedisSessionConfig.getRedisSessionRepository();
            }
            return RedisSessionConfig.getRedisOperationsSessionRepository();
        }
    }

/*
    This can be removed once we start using single data source for session
*/

    @Override
    public Session createSession() {
        return getSessionRepositoryBean().createSession();
    }

    @Override
    public void save(Session session) {
        org.springframework.session.SessionRepository sessionRepository = getSessionRepositoryBean();
        if ((sessionRepository instanceof MongoOperationsSessionRepository
                && session instanceof MongoSession)
                || (sessionRepository instanceof RedisOperationsSessionRepository
                        && session instanceof org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession)) {
            log.debug("session and sessionRepository have same database instance");
            sessionRepository.save(session);
            saveSessionAsSecondary(session);

        } else if (session instanceof org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession
                && sessionRepository instanceof MongoOperationsSessionRepository) {
            log.debug("session instance of redis type and sessionRepository instance of mongo type");
            log.info("session instance of redis type and sessionRepository instance of mongo type");
            SessionData SessionData = SessionConverter
                    .convertToSessionData((org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession) session);
            MongoSessionConfig.getMongoSessionRepository().saveAsSecondary(SessionData);
        } else if (session instanceof MongoSession
                && sessionRepository instanceof RedisOperationsSessionRepository) {
            log.debug("session instance of mongo type and sessionRepository instance of redis type");
            log.info("session instance of mongo type and sessionRepository instance of redis type");
            SessionData SessionData = SessionConverter.convertToSessionData((MongoSession) session);
            RedisSessionConfig.getRedisSessionRepository().saveAsSecondary(SessionData);
        }
    }

    @Override
    public Session findById(String s) {
        org.springframework.session.SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Reading session from {}", sessionRepository);
        return sessionRepository.findById(s);
    }

    @Override
    public void deleteById(String s) {
        getSessionRepositoryBean().deleteById(s);
        deleteSessionsFromSecondary(s);
    }


    private void saveSessionAsSecondary(Session session){
        boolean secondaryStorageEnabled =  SessionUtil.isSecondaryStorageEnabled();
        if(secondaryStorageEnabled && session instanceof org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession){
            SessionData SessionData = SessionConverter.convertToSessionData((org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession) session);
            MongoSessionConfig.getMongoSessionRepository().saveAsSecondary(SessionData);
        }else  if(secondaryStorageEnabled && session instanceof MongoSession){
            SessionData SessionData = SessionConverter.convertToSessionData((MongoSession) session);
            RedisSessionConfig.getRedisSessionRepository().saveAsSecondary(SessionData);
        }
    }

    private void deleteSessionsFromSecondary(String sessionId){
       boolean secondaryStorageEnabled =  SessionUtil.isSecondaryStorageEnabled();
        SessionStore storageType = SessionUtil.getPrimaryStorage();
       if(secondaryStorageEnabled && SessionStore.MONGO.equals(storageType)){
           RedisSessionConfig.getRedisSessionRepository().deleteById(sessionId);
       }else if(secondaryStorageEnabled && SessionStore.REDIS.equals(storageType)){
           MongoSessionConfig.getMongoSessionRepository().deleteById(sessionId);
       }
    }



}
