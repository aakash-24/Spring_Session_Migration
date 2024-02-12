package org.spring.framework.data.redis;

import com.dt.platform.session.DTSessionData;
import org.spring.framework.session.SessionData;
import lombok.experimental.UtilityClass;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description: This is converter for redisSession Object to Mongo and vice versa. Kept in package
 * org.springframework.session.data.redis because RedisSession class is only accessible in this
 * package. Date 12/06/23-04:11 pm
 *
 * @author aakashjain
 * @since
 */

@UtilityClass
public class SessionConverter {

    public SessionData convertToSessionData(MongoSession mongoSession) {
        return SessionData.builder().id(mongoSession.getId()).attributes(getAttributes(mongoSession))
                .maxInactiveInterval(mongoSession.getMaxInactiveInterval()).build();
    }

    public SessionData convertToSessionData(RedisOperationsSessionRepository.RedisSession redisSession) {
        return SessionData.builder().id(redisSession.getId()).attributes(getAttributes(redisSession))
                .maxInactiveInterval(redisSession.getMaxInactiveInterval())
                .createdMillis(redisSession.getCreationTime().toEpochMilli()).build();
    }

    private Map<String, Object> getAttributes(MongoSession mongoSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = mongoSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, mongoSession.getAttribute(attribute));
        });
        return attributesMap;
    }

    private Map<String, Object> getAttributes(RedisOperationsSessionRepository.RedisSession redisSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = redisSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, redisSession.getAttribute(attribute));
        });
        return attributesMap;
    }

}
