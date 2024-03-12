package org.springframework.session;


import org.framework.data.SpringSessionData;

public interface ISpringSessionOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
