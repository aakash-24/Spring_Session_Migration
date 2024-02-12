package org.spring.framework.session.utils;


import lombok.experimental.UtilityClass;

import org.spring.framework.session.SessionStore;

/*
Note : This class can be removed once we are done with secondary database for session stage.
*/
@UtilityClass
public class SessionUtil {

    public static SessionStore getPrimaryStorage() {
        String storageType = CMSPropertyMapUtil.getConfigValueByKey(CommonConstants.GLOBAL_SESSION_STORAGE);
        if (StringUtils.isNotEmpty(storageType) && storageType.equals(SessionStore.MONGO.name())) {
            return SessionStore.MONGO;
        } else {
            return SessionStore.REDIS;
        }
    }

    public static  boolean isSecondaryStorageEnabled(){
      return BooleanUtils.isTrue(CMSPropertyMapUtil.getConfigValueByKey(CommonConstants.GLOBAL_SESSION_SECONDARY_STORAGE_ENABLED));
    }


}
