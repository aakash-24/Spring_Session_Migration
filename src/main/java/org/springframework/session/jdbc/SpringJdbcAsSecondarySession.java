package org.springframework.session.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.framework.data.SpringSessionData;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISpringSessionOperation;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Instant;

@Slf4j
public class SpringJdbcAsSecondarySession extends JdbcIndexedSessionRepository implements ISpringSessionOperation {
    public SpringJdbcAsSecondarySession(JdbcOperations jdbcOperations, TransactionOperations transactionOperations) {
        super(jdbcOperations, transactionOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("Going to save session as secondary in jdbc");
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
        log.info("Session created as secondary in jdbc");
    }
    private void addAttributesAndSave(SpringSessionData springSessionData, JdbcSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}