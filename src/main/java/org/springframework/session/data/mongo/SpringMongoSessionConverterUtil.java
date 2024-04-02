package org.springframework.session.data.mongo;

import org.framework.data.SpringSessionData;
import lombok.experimental.UtilityClass;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SpringMongoSessionConverterUtil provides utility methods for converting sessions to SpringSessionData objects.
 * @author Hunny Kalra, Aakash Jain, Shishir Pandey, Hardik Sharma
 */
@UtilityClass
public class SpringMongoSessionConverterUtil {

    /**
     * Converts a Session object to a SpringSessionData object.
     * @param session The session object to be converted.
     * @return The converted SpringSessionData object.
     */
    public static SpringSessionData convertToSessionData(Session session) {
        final MongoSession mongoSession = (MongoSession) session;
        return SpringSessionData.builder()
                .id(mongoSession.getId())
                .attributes(getAttributes(mongoSession))
                .createdMillis(mongoSession.getCreationTime().toEpochMilli())
                .maxInactiveInterval(mongoSession.getMaxInactiveInterval())
                .build();
    }

    /**
     * Retrieves attributes from a MongoSession and returns them as a map.
     * @param mongoSession The MongoSession from which to retrieve attributes.
     * @return A map containing the attributes of the MongoSession.
     */
    private static Map<String, Object> getAttributes(MongoSession mongoSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = mongoSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, mongoSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}

