package org.springframework.session.data;

import org.spring.framework.data.SpringSessionData;

public interface ISpringSessionOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
