package org.spring.framework.data;

import org.spring.framework.session.SessionData;

public interface ISessionOperations {

    void saveAsSecondary(SessionData sessionData);
}
