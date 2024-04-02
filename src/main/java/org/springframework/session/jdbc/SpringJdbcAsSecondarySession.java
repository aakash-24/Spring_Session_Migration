package org.springframework.session.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.spring.framework.data.SpringSessionData;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISessionSaveAsSecondaryOperation;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Instant;

@Slf4j
public class SpringJdbcAsSecondarySession extends JdbcIndexedSessionRepository implements ISessionSaveAsSecondaryOperation {
    public SpringJdbcAsSecondarySession(JdbcOperations jdbcOperations, TransactionOperations transactionOperations) {
        super(jdbcOperations, transactionOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("RDBMS is set to save secondary session");
        JdbcSession session = findById(springSessionData.getId());
        if (session != null) {
            addAttributesAndSave(springSessionData, session);
            return;
        }
        MapSession mapSession = new MapSession();
        mapSession.setId(springSessionData.getId());
        JdbcSession jdbcSession = new JdbcSession(mapSession,springSessionData.getId(),true);
        jdbcSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        jdbcSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(springSessionData, jdbcSession);
        log.info("Session is created in RDBMS");
    }
    private void addAttributesAndSave(SpringSessionData springSessionData, JdbcSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}
