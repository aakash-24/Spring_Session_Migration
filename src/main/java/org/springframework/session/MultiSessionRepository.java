package org.springframework.session;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.framework.data.SpringSessionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.data.mongo.SpringMongoSessionConfig;
import org.springframework.session.data.mongo.SpringMongoSessionConverterUtil;
import org.springframework.session.data.redis.SpringRedisSessionConfig;
import org.springframework.session.data.redis.SpringRedisSessionConverterUtil;
import org.springframework.session.jdbc.SpringRdbmsSessionConfig;
import org.springframework.session.jdbc.SpringRdbmsSessionConverterUtil;

/**
 * MultiSessionRepository is a session repository that supports multiple storage options as primary and secondary storage.
 * It determines the primary storage based on configuration and delegates session operations accordingly.
 * @author Hardik Sharma
 */
@Slf4j
public class MultiSessionRepository implements SessionRepository {

    @Value("${spring.session.primary_storage.name}")
    private String PRIMARYSTORAGE;

    @Value("${spring.session.secondary_storage.enabled:false}")
    private boolean SECONDARY_STORAGE_ENABLED;

    @Value("${spring.session.secondary_storage.name}")
    private String SECONDARYSTORAGE;

    private SpringMongoSessionConfig springMongoSessionConfig;

    private SpringRedisSessionConfig springRedisSessionConfig;

    private SpringRdbmsSessionConfig springRdbmsSessionConfig;

    /**
     * Retrieves the appropriate session repository based on the primary storage configuration.
     * @return The session repository bean for the configured primary storage.
     */
    public SessionRepository getSessionRepositoryBean() {
        if(StringUtils.isNotEmpty(PRIMARYSTORAGE)) {
            log.info(PRIMARYSTORAGE + " is primary storage");
            switch (PRIMARYSTORAGE) {
                case "MONGO":
                    return springMongoSessionConfig.getMongoOperationsSessionRepository();
                case "REDIS":
                    return springRedisSessionConfig.getRedisOperationsSessionRepository();
                case "RDBMS":
                    return springRdbmsSessionConfig.getJdbcIndexedSessionRepository();
                default:
                    throw new IllegalArgumentException("Does not match the given primary storage");
            }
        }
        else {
            throw new IllegalArgumentException("No primary storage configuration found");
        }
    }

    /**
     * Creates a new session.
     * @return The newly created session.
     */
    @Override
    public Session createSession() {
        log.debug("Storage not configured");
        return getSessionRepositoryBean().createSession();
    }

    /**
     * Saves a session.
     * @param session The session to be saved.
     */
    @Override
    public void save(Session session) {
        SessionRepository sessionRepository = getSessionRepositoryBean();
        sessionRepository.save(session);
        if(secondaryStorageIsEnabled()) {
            saveSessionAsSecondary(session);
        }
    }

    /**
     * Checks if secondary storage is enabled.
     * @return True if secondary storage is enabled, otherwise false.
     */
    private boolean secondaryStorageIsEnabled() {
        return SECONDARY_STORAGE_ENABLED;
    }

    /**
     * Saves a session to secondary storage.
     * @param session The session to be saved.
     */
    private void saveSessionAsSecondary(Session session) {
        if (StringUtils.isNotEmpty(SECONDARYSTORAGE)) {
            SpringSessionData springSessionData = multiSessionConverter(session);
            multiSessionSaveAsSecondary(springSessionData);
        }
        else {
            throw new IllegalArgumentException("No secondary storage configuration found");
        }
    }

    /**
     * Saves the given SpringSessionData object to a secondary storage based on the configured secondary storage type.
     *
     * @param springSessionData The SpringSessionData object to be saved to secondary storage.
     * @throws IllegalArgumentException If the configured secondary storage type is not supported.
     */
    private void multiSessionSaveAsSecondary(SpringSessionData springSessionData) {
        switch (SECONDARYSTORAGE.toUpperCase()){
            case "MONGO":
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().saveAsSecondary(springSessionData);
                return;
            case "REDIS":
                springRedisSessionConfig.getSpringRedisOperationsSessionRepository().saveAsSecondary(springSessionData);
                return;
            case "RDBMS":
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().saveAsSecondary(springSessionData);
                return;
            default:
                throw new IllegalArgumentException("cannot save as secondary storage");

        }
    }

    /**
     * Converts a Session object to a SpringSessionData object based on the configured primary storage type.
     *
     * @param session The Session object to be converted.
     * @return The converted SpringSessionData object.
     * @throws IllegalArgumentException If the configured primary storage type is not supported.
     */
    private SpringSessionData multiSessionConverter(Session session) {
        switch (PRIMARYSTORAGE.toUpperCase()) {
            case "MONGO":
                return SpringMongoSessionConverterUtil.convertToSessionData(session);
            case "REDIS":
                return SpringRedisSessionConverterUtil.convertToSessionData(session);
            case "RDBMS":
                return SpringRdbmsSessionConverterUtil.convertToSessionData(session);
            default:
                throw new IllegalArgumentException("Session can not be converted");
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
        if(secondaryStorageIsEnabled()){
            deleteSessionsFromSecondary(id);
        }
    }

    /**
     * Deletes sessions from secondary storage.
     * @param sessionId The ID of the session to delete from secondary storage.
     */
    private void deleteSessionsFromSecondary(String sessionId) {
        switch (SECONDARYSTORAGE.toUpperCase()) {
            case "MONGO":
                log.info("Going to delete Mongo as secondary session");
                springMongoSessionConfig.getSpringMongoOperationsSessionRepository().deleteById(sessionId);
                break;
            case "REDIS":
                log.info("Going to delete Redis as secondary session");
                springRedisSessionConfig.getRedisOperationsSessionRepository().deleteById(sessionId);
                break;
            case "RDBMS":
                log.info("Going to delete Rdbms as secondary session");
                springRdbmsSessionConfig.getSpringJdbcOperationsSessionRepository().deleteById(sessionId);
                break;
            default:
                log.error("Session cannot be deleted");
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
