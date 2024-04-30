package org.springframework.session.jdbc;

import lombok.experimental.UtilityClass;
import org.framework.data.SpringSessionData;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SpringRdbmsSessionConverterUtil provides utility methods for converting sessions to SpringSessionData objects.
 * @author Hardik Sharma
 */
@UtilityClass
public class SpringRdbmsSessionConverterUtil {

    /**
     * Converts a Session object to a SpringSessionData object.
     * @param session The session object to be converted.
     * @return The converted SpringSessionData object.
     */
    public static SpringSessionData convertToSessionData(Session session) {
        final JdbcIndexedSessionRepository.JdbcSession jdbcSession = (JdbcIndexedSessionRepository.JdbcSession) session;
        return SpringSessionData.builder()
                .id(jdbcSession.getId())
                .attributes(getAttributes(jdbcSession))
                .maxInactiveInterval(jdbcSession.getMaxInactiveInterval())
                .build();
    }

    /**
     * Retrieves attributes from a JdbcSession and returns them as a map.
     * @param jdbcSession The JdbcSession from which to retrieve attributes.
     * @return A map containing the attributes of the JdbcSession.
     */
    private static Map<String, Object> getAttributes(JdbcIndexedSessionRepository.JdbcSession jdbcSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = jdbcSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, jdbcSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}

