package org.springframework.session.jdbc;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionOperations;

/**
 * SpringRdbmsSessionConfig provides configuration for Spring sessions stored in a relational database.
 * It initializes and configures SpringRdbmsAsSecondarySession and JdbcIndexedSessionRepository.
 * @author Hunny Kalra, Aakash Jain, Shishir Pandey, Hardik Sharma
 */
public class SpringRdbmsSessionConfig {

    private static JdbcIndexedSessionRepository jdbcIndexedSessionRepository;

    private final JdbcOperations jdbcOperations;

    private SpringRdbmsAsSecondarySession springJdbcAsSecondarySession;

    private final TransactionOperations transactionOperations;

    /**
     * Constructor for SpringRdbmsSessionConfig.
     * @param jdbcOperations The JdbcOperations object to interact with the database.
     * @param transactionOperations The TransactionOperations object to manage transactions.
     */
    public SpringRdbmsSessionConfig(JdbcOperations jdbcOperations, TransactionOperations transactionOperations){
        this.jdbcOperations = jdbcOperations;
        this.transactionOperations = transactionOperations;
    }

    /**
     * Retrieves the configured SpringRdbmsAsSecondarySession.
     * @return The configured SpringRdbmsAsSecondarySession.
     */
    public SpringRdbmsAsSecondarySession getSpringJdbcOperationsSessionRepository(){
        if (springJdbcAsSecondarySession != null) {
            return springJdbcAsSecondarySession;
        }
        springJdbcAsSecondarySession =
                new SpringRdbmsAsSecondarySession(jdbcOperations, transactionOperations);
        return springJdbcAsSecondarySession;
    }

    /**
     * Retrieves the configured JdbcIndexedSessionRepository.
     * @return The configured JdbcIndexedSessionRepository.
     */
    public JdbcIndexedSessionRepository getJdbcIndexedSessionRepository(){
        if(jdbcIndexedSessionRepository != null){
            return jdbcIndexedSessionRepository;
        }
        jdbcIndexedSessionRepository = new JdbcIndexedSessionRepository(jdbcOperations, transactionOperations);
        return jdbcIndexedSessionRepository;
    }
}
