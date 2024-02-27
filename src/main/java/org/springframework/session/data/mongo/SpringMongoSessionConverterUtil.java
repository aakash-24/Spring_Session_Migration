package org.springframework.session.data.mongo;

import lombok.experimental.UtilityClass;
import org.spring.framework.data.SpringSessionData;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SpringMongoSessionConverterUtil {
    public static SpringSessionData convertToSessionData(Session session) {
        final MongoSession s = (MongoSession) session;
        return SpringSessionData.builder().id(s.getId()).attributes(getAttributes(s))
                .maxInactiveInterval(s.getMaxInactiveInterval()).build();
    }

    private static Map<String, Object> getAttributes(MongoSession mongoSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = mongoSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, mongoSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}
