package org.springframework.session;

import org.spring.framework.data.SpringSessionData;

public interface ISpringSessionOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
