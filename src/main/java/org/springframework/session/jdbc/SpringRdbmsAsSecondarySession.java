package org.springframework.session.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.framework.data.SpringSessionData;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.session.MapSession;
import org.springframework.session.ISessionSaveAsSecondaryOperation;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Instant;

/**
 * SpringRdbmsAsSecondarySession extends JdbcIndexedSessionRepository and implements ISessionSaveAsSecondaryOperation.
 * This class is responsible for saving session data as a secondary operation in a relational database.
 * @author Hunny Kalra, Aakash Jain, Shishir Pandey, Hardik Sharma
 */
@Slf4j
public class SpringRdbmsAsSecondarySession extends JdbcIndexedSessionRepository implements ISessionSaveAsSecondaryOperation {

    /**
     * Constructor for SpringRdbmsAsSecondarySession.
     * @param jdbcOperations The JdbcOperations object to interact with the database.
     * @param transactionOperations The TransactionOperations object to manage transactions.
     */
    public SpringRdbmsAsSecondarySession(JdbcOperations jdbcOperations, TransactionOperations transactionOperations) {
        super(jdbcOperations, transactionOperations);
    }

    /**
     * Saves session data as secondary in a relational database.
     * @param springSessionData The SpringSessionData object containing session data to be saved.
     */
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
        JdbcSession jdbcSession = new JdbcSession(mapSession, springSessionData.getId(), true);
        jdbcSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        jdbcSession.setLastAccessedTime(Instant.now());
        addAttributesAndSave(springSessionData, jdbcSession);
        log.info("Session is created in RDBMS");
    }

    /**
     * Adds attributes to the session and saves it.
     * @param springSessionData The SpringSessionData object containing attributes to be added.
     * @param session The JdbcSession object to which attributes are added.
     */
    private void addAttributesAndSave(SpringSessionData springSessionData, JdbcSession session) {
        springSessionData.getAttributes().forEach(session::setAttribute);
        super.save(session);
    }
}
