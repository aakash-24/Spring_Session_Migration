package org.springframework.session.data.redis;

import org.framework.data.SpringSessionData;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISpringSessionOperation;

import java.time.Instant;

@Slf4j
public class SpringRedisAsSecondarySession extends RedisIndexedSessionRepository implements ISpringSessionOperation {

    public SpringRedisAsSecondarySession(RedisOperations<String, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("Going to save session as secondary in redis");
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
        addAttributesAndSave(springSessionData, redisSession);
        log.info("Session created as secondary in redis");
    }


    private void addAttributesAndSave(SpringSessionData springSessionData, RedisSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}
