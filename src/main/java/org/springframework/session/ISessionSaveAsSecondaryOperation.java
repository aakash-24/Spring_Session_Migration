package org.springframework.session;

import org.spring.framework.data.SpringSessionData;

public interface ISessionSaveAsSecondaryOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
