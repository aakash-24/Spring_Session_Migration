package org.springframework.session;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.framework.data.SpringDataStore;
import org.framework.data.SpringSessionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.SpringMongoSessionConfig;
import org.springframework.session.data.mongo.SpringMongoSessionConverterUtil;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.SpringRedisSessionConfig;
import org.springframework.session.data.redis.SpringRedisSessionConverterUtil;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.SpringRdbmsSessionConverterUtil;
import org.springframework.session.jdbc.SpringRdbmsSessionConfig;

/**
 * MultiSessionRepository is a session repository that supports multiple storage options as primary and secondary storage.
 * It determines the primary storage based on configuration and delegates session operations accordingly.
 * @author Hunny Kalra, Aakash Jain, Shishir Pandey, Hardik Sharma
 */
@Slf4j
public class MultiSessionRepository implements SessionRepository {

    @Value("${spring.session.primary_storage}")
    private String primarystorage;

    @Value("${spring.session.secondary_storageIsEnabled:false}")
    private boolean secondarystorageIsEnabled;

    @Value("${spring.session.secondary_storage}")
    private String secondarystorage;

    private SpringMongoSessionConfig springMongoSessionConfig;

    private SpringRedisSessionConfig springRedisSessionConfig;

    private SpringRdbmsSessionConfig springRdbmsSessionConfig;

    /**
     * Retrieves the appropriate session repository based on the primary storage configuration.
     * @return The session repository bean for the configured primary storage.
     */
    public SessionRepository getSessionRepositoryBean() {
        if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.MONGO.name())) {
            log.info("MONGO is primary storage");
            return springMongoSessionConfig.getMongoOperationsSessionRepository();
        } else if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.REDIS.name())){
            log.info("REDIS is primary storage");
            return springRedisSessionConfig.getRedisOperationsSessionRepository();
        } else if(StringUtils.isNotEmpty(primarystorage) && primarystorage.equals(SpringDataStore.RDBMS.name())){
            log.info("RDBMS is primary storage");
            return springRdbmsSessionConfig.getJdbcIndexedSessionRepository();
        }
        log.error("No Primary Storage Configured");
        return null;
    }

    /**
     * Creates a new session.
     * @return The newly created session.
     */
    @Override
    public Session createSession() {
        log.debug("Storage not configured");
        log.info("Going to create session");
        return getSessionRepositoryBean().createSession();
    }

    /**
     * Saves a session.
     * @param session The session to be saved.
     */
    @Override
    public void save(Session session) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to save session");
        sessionRepository.save(session);
        if(secondaryStorageIsEnabled())
            saveSessionAsSecondary(session);
    }

    /**
     * Checks if secondary storage is enabled.
     * @return True if secondary storage is enabled, otherwise false.
     */
    private boolean secondaryStorageIsEnabled() {
        return secondarystorageIsEnabled;
    }

    /**
     * Saves a session to secondary storage.
     * @param session The session to be saved.
     */
    private void saveSessionAsSecondary(Session session) {
        log.debug("Secondary Storage is enabled but spring.session.secondary_storage is NULL ");
        if(getSessionRepositoryBean() instanceof MongoIndexedSessionRepository ) {
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.RDBMS.name())){
                log.info("Secondary Storage Storage is JDBC");
                SpringSessionData springSessionData = SpringMongoSessionConverterUtil.convertToSessionData(session);
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.REDIS.name())){
                log.info("Secondary Storage Storage is REDIS");
                SpringSessionData springSessionData = SpringMongoSessionConverterUtil.convertToSessionData(session);
                springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }else if(getSessionRepositoryBean() instanceof RedisIndexedSessionRepository){
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.MONGO.name())){
                log.info("Secondary Storage Storage is MONGO");
                SpringSessionData springSessionData = SpringRedisSessionConverterUtil.convertToSessionData(session);
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.RDBMS.name())){
                log.info("Secondary Storage Storage is JDBC");
                SpringSessionData springSessionData = SpringRedisSessionConverterUtil.convertToSessionData(session);
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }else if (getSessionRepositoryBean() instanceof JdbcIndexedSessionRepository){
            if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.REDIS.name())){
                log.info("Secondary Storage Storage is REDIS");
                SpringSessionData springSessionData = SpringRdbmsSessionConverterUtil.convertToSessionData(session);
                springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
            else if(!secondarystorage.isEmpty() && secondarystorage.equals(SpringDataStore.MONGO.name())){
                log.info("Secondary Storage Storage is MONGO");
                SpringSessionData springSessionData = SpringRdbmsSessionConverterUtil.convertToSessionData(session);
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
            }
        }
    }

    /**
     * Finds a session by its ID.
     * @param id The ID of the session to find.
     * @return The session if found, otherwise null.
     */
    @Override
    public Session findById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to find session");
        return sessionRepository.findById(id);
    }

    /**
     * Deletes a session by its ID.
     * @param id The ID of the session to delete.
     */
    @Override
    public void deleteById(String id) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        log.info("Going to delete session");
        sessionRepository.deleteById(id);
        if(secondaryStorageIsEnabled())
            deleteSessionsFromSecondary(id);
    }

    /**
     * Deletes sessions from secondary storage.
     * @param sessionId The ID of the session to delete from secondary storage.
     */
    private void deleteSessionsFromSecondary(String sessionId) {
        if((secondarystorage).equals(SpringDataStore.MONGO.name())){
            log.info("Going to delete MONGO as secondary session");
            springMongoSessionConfig.getSpringMongoOperationsSessionRepository().deleteById(sessionId);
        }
        else if((secondarystorage).equals(SpringDataStore.REDIS.name())){
            log.info("Going to delete REDIS as secondary session");
            springRedisSessionConfig.getRedisOperationsSessionRepository().deleteById(sessionId);
        }
        else if((secondarystorage).equals(SpringDataStore.RDBMS.name())){
            log.info("Going to delete JDBC as secondary session");
            springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().deleteById(sessionId);
        }
    }

    /**
     * Sets the SpringMongoSessionConfig bean.
     * @param springMongoSessionConfig The SpringMongoSessionConfig bean to set.
     */
    public void setSpringMongoSessionConfigs(SpringMongoSessionConfig springMongoSessionConfig) {
        this.springMongoSessionConfig = springMongoSessionConfig;
    }

    /**
     * Sets the SpringRedisSessionConfig bean.
     * @param springRedisSessionConfig The SpringRedisSessionConfig bean to set.
     */
    public void setRedisSessionConfig(SpringRedisSessionConfig springRedisSessionConfig) {
        this.springRedisSessionConfig = springRedisSessionConfig;
    }

    /**
     * Sets the SpringRdbmsSessionConfig bean.
     * @param springRdbmsSessionConfig The SpringRdbmsSessionConfig bean to set.
     */
    public void setRdbmsSessionConfig(SpringRdbmsSessionConfig springRdbmsSessionConfig){
        this.springRdbmsSessionConfig = springRdbmsSessionConfig;
    }
}
