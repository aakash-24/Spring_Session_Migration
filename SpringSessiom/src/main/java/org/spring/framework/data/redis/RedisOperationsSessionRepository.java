package org.spring.framework.data.redis;

import com.dt.platform.session.DTSessionData;
import com.dt.platform.session.policy.HttpSessionMaxAgePolicy;
import com.dt.platform.utils.http.HttpUtils;
import org.spring.framework.session.SessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.data.ISessionOperations;

import java.time.Duration;
import java.time.Instant;


@Slf4j
public class RedisOperationsSessionRepository extends org.springframework.session.data.redis.RedisOperationsSessionRepository
        implements ISessionOperations {

    private final HttpSessionMaxAgePolicy httpSessionMaxAgePolicy;


    public RedisOperationsSessionRepository(RedisOperations<Object, Object> sessionRedisOperations,
                                            HttpSessionMaxAgePolicy httpSessionMaxAgePolicy) {
        super(sessionRedisOperations);
        this.httpSessionMaxAgePolicy = httpSessionMaxAgePolicy;
    }

    @Override
    public RedisSession createSession() {
        log.debug("Going to customize session while creation.");
        RedisSession redisSession = new RedisSession();
        redisSession.setMaxInactiveInterval(
                Duration.ofSeconds(httpSessionMaxAgePolicy.getApplicableMaxInactiveInterval(
                        HttpUtils.resolveHttpRequest(), HttpUtils.resolveHttpResponse())));
        log.debug("Session with custom max age policy created.");
        return redisSession;
    }

    @Override
    public void save(RedisSession redisSession) {
        log.debug("Session created as primary in redis");
        log.info("Session created as primary in redis");
        super.save(redisSession);
    }

    @Override
    public void saveAsSecondary(SessionData dtSessionData) {
        log.debug("Going to save session as secondary in redis");
        RedisSession session = findById(dtSessionData.getId());
        if (session != null && !session.isNew()) {
            addAttributesAndSave(dtSessionData, session);
            return;
        }
        MapSession mapSession = new MapSession();
        mapSession.setId(dtSessionData.getId());
        RedisSession redisSession = new RedisSession(mapSession);
        redisSession.setMaxInactiveInterval(dtSessionData.getMaxInactiveInterval());
        redisSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(dtSessionData, redisSession);
        log.debug("Session created as secondary in redis");
    }

    private void addAttributesAndSave(SessionData dtSessionData, RedisSession session) {
        dtSessionData.getAttributes().forEach((key, value) -> session.setAttribute(key, value));
        super.save(session);
    }
}
