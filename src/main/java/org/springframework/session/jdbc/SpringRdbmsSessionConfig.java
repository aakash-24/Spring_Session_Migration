package org.springframework.session.jdbc;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionOperations;

public class SpringRdbmsSessionConfig {

    private static JdbcIndexedSessionRepository jdbcIndexedSessionRepository;

    private final JdbcOperations jdbcOperations;

    private SpringJdbcAsSecondarySession springJdbcAsSecondarySession;

    private final TransactionOperations transactionOperations;

    public SpringRdbmsSessionConfig(JdbcOperations jdbcOperations, TransactionOperations transactionOperations){
        this.jdbcOperations = jdbcOperations;
        this.transactionOperations = transactionOperations;
    }

    public SpringJdbcAsSecondarySession getSpringJdbcOperationsSessionRepository(){
        if (springJdbcAsSecondarySession != null) {
            return springJdbcAsSecondarySession;
        }
        springJdbcAsSecondarySession =
                new SpringJdbcAsSecondarySession(jdbcOperations,transactionOperations);
        return springJdbcAsSecondarySession;
    }
    public JdbcIndexedSessionRepository getJdbcIndexedSessionRepository(){
        if(jdbcIndexedSessionRepository != null){
            return jdbcIndexedSessionRepository;
        }
        jdbcIndexedSessionRepository = new JdbcIndexedSessionRepository(jdbcOperations,transactionOperations);
        return jdbcIndexedSessionRepository;
    }
}
