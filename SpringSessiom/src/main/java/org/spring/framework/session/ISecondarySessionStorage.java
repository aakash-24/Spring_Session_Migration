package org.spring.framework.session;

import org.spring.framework.data.ISessionOperations;

public interface ISecondarySessionStorage {

    void saveSecondarySession(SessionData sessionData, ISessionOperations iSessionOperations);
}
