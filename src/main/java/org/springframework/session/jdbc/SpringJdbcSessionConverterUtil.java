package org.springframework.session.jdbc;

import lombok.experimental.UtilityClass;
import org.spring.framework.data.SpringSessionData;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SpringJdbcSessionConverterUtil {
    public static SpringSessionData convertToSessionData(Session session) {
        final JdbcIndexedSessionRepository.JdbcSession jdbcSession = (JdbcIndexedSessionRepository.JdbcSession) session;
        return SpringSessionData.builder().id(jdbcSession.getId()).attributes(getAttributes(jdbcSession))
                .maxInactiveInterval(jdbcSession.getMaxInactiveInterval()).build();
    }

    private static Map<String, Object> getAttributes(JdbcIndexedSessionRepository.JdbcSession jdbcSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = jdbcSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, jdbcSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}
