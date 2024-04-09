package org.springframework.session.data.redis;

import org.framework.data.SpringSessionData;
import lombok.experimental.UtilityClass;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
/**
 * SpringRedisSessionConverterUtil provides utility methods for converting sessions to SpringSessionData objects.
 * @author Hunny Kalra
 */
public class SpringRedisSessionConverterUtil {

    /**
     * Converts a Session object to a SpringSessionData object.
     * @param session The session object to be converted.
     * @return The converted SpringSessionData object.
     */
    public static SpringSessionData convertToSessionData(Session session) {
        final RedisIndexedSessionRepository.RedisSession redisSession
                = (RedisIndexedSessionRepository.RedisSession) session;
        return SpringSessionData.builder()
                .id(redisSession.getId())
                .attributes(getAttributes(redisSession))
                .maxInactiveInterval(redisSession.getMaxInactiveInterval())
                .createdMillis(redisSession.getCreationTime().toEpochMilli())
                .build();
    }

    /**
     * Retrieves attributes from a RedisSession and returns them as a map.
     * @param redisSession The RedisSession from which to retrieve attributes.
     * @return A map containing the attributes of the RedisSession.
     */
    private static Map<String, Object> getAttributes(RedisIndexedSessionRepository.RedisSession redisSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = redisSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, redisSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}
