package org.springframework.session.data.redis;

import lombok.extern.slf4j.Slf4j;
import org.spring.framework.data.SpringSessionData;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISessionSaveAsSecondaryOperation;

import java.time.Instant;

@Slf4j
public class SpringRedisAsSecondarySession extends RedisIndexedSessionRepository implements ISessionSaveAsSecondaryOperation {

    public SpringRedisAsSecondarySession(RedisOperations<String, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("REDIS is set to save secondary session");
        RedisSession session = findById(springSessionData.getId());;
        if (session != null) {
            addAttributesAndSave(springSessionData, session);
            return;
        }
        MapSession mapSession = new MapSession();
        mapSession.setId(springSessionData.getId());
        RedisSession redisSession = new RedisSession(mapSession,true);
        redisSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        redisSession.setLastAccessedTime(Instant.now());
        redisSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(springSessionData, redisSession);
        log.info("Secondary Session is created in REDIS");
    }


    private void addAttributesAndSave(SpringSessionData springSessionData, RedisSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}
