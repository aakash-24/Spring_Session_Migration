package org.springframework.session;


import org.framework.data.SpringSessionData;

/**
 * ISessionSaveAsSecondary defines a contract for saving session data as a secondary operation.
 * Implementing classes should provide an implementation for the saveAsSecondary method.
 * @author Hardik Sharma
 */
public interface ISessionSaveAsSecondary {

    /**
     * Saves session data as secondary.
     * @param springSessionData The SpringSessionData object containing session data to be saved.
     */
    void saveAsSecondary(SpringSessionData springSessionData);
}
