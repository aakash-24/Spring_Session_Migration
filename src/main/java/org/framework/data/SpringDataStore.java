package org.framework.data;

/**
 * SpringDataStore enum represents the supported data stores for Spring sessions.
 * @author Shishir Pandey
 */
public enum SpringDataStore {

    /**
     * MongoDB data store.
     */
    MONGO,

    /**
     * Redis data store.
     */
    REDIS,

    /**
     * RDBMS (Relational Database Management System) data store.
     */
    RDBMS;
}

