package org.springframework.session.data.redis;

import lombok.extern.slf4j.Slf4j;
import org.spring.framework.SpringSessionData;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.data.ISpringSessionOperation;

import java.time.Instant;

@Slf4j
public class OpenSourceRedisOperationsSessionRepository extends RedisIndexedSessionRepository implements ISpringSessionOperation {

    public OpenSourceRedisOperationsSessionRepository(RedisOperations<String, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }


    @Override
    public RedisSession createSession() {
        return super.createSession();
    }


    @Override
    public void save(RedisSession session) {
        super.save(session);
    }


    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("Going to save session as secondary in redis");
        log.debug("Going to save session as secondary in redis");
        RedisSession session = findById(springSessionData.getId());;
        if (session != null) {
            addAttributesAndSave(springSessionData, session);
            return;
        }
        MapSession mapSession = new MapSession();
        mapSession.setId(springSessionData.getId());
        RedisSession redisSession = new RedisSession(mapSession,false);
        redisSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        redisSession.setLastAccessedTime(Instant.now());
        redisSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(springSessionData, redisSession);
        log.debug("Session created as secondary in redis");
    }


    private void addAttributesAndSave(SpringSessionData springSessionData, RedisSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}
