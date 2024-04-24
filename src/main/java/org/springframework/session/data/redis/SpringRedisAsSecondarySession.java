package org.springframework.session.data.redis;

import org.framework.data.SpringSessionData;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISessionSaveAsSecondary;

import java.time.Instant;

/**
 * SpringRedisAsSecondarySession extends RedisIndexedSessionRepository and implements ISessionSaveAsSecondaryOperation.
 * This class is responsible for saving session data as a secondary operation in Redis.
 * @author Aakash Jain
 */
@Slf4j
public class SpringRedisAsSecondarySession extends RedisIndexedSessionRepository implements ISessionSaveAsSecondary {

    /**
     * Constructor for SpringRedisAsSecondarySession.
     * @param sessionRedisOperations The RedisOperations object to interact with Redis.
     */
    public SpringRedisAsSecondarySession(RedisOperations<String, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    /**
     * Saves session data as secondary in Redis.
     * @param springSessionData The SpringSessionData object containing session data to be saved.
     */
    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("Redis is set to save secondary session");
        RedisSession session = findById(springSessionData.getId());
        if (session != null) {
            addAttributesAndSave(springSessionData, session);
            return;
        }
        MapSession mapSession = new MapSession();
        mapSession.setId(springSessionData.getId());
        RedisSession redisSession = new RedisSession(mapSession, true);
        redisSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());

        redisSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(springSessionData, redisSession);
        log.info("Secondary Session is created in Redis");
    }

    /**
     * Adds attributes to the session and saves it.
     * @param springSessionData The SpringSessionData object containing attributes to be added.
     * @param session The RedisSession object to which attributes are added.
     */
    private void addAttributesAndSave(SpringSessionData springSessionData, RedisSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}

