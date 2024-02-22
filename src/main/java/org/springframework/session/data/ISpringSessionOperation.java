package org.springframework.session.data;

import org.spring.framework.SpringSessionData;

public interface ISpringSessionOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
