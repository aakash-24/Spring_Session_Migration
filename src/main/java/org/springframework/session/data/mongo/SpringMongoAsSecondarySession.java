package org.springframework.session.data.mongo;


import lombok.extern.slf4j.Slf4j;
import org.spring.framework.data.SpringSessionData;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.ISessionSaveAsSecondaryOperation;

import java.util.Map;

@Slf4j
public class SpringMongoAsSecondarySession extends MongoIndexedSessionRepository
        implements ISessionSaveAsSecondaryOperation {
    public SpringMongoAsSecondarySession(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("MONGO is set to save secondary session");
        MongoSession mongoSession = findById(springSessionData.getId());
        if(mongoSession==null){
            mongoSession = new MongoSession(springSessionData.getId(),
                    springSessionData.getMaxInactiveInterval().getSeconds());
        }else{
            mongoSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        }
        Map<String,Object> keys = springSessionData.getAttributes();
        for(Map.Entry<String,Object> key : keys.entrySet()){
            mongoSession.setAttribute(key.getKey(), key.getValue());
        }
        mongoSession.setCreationTime(springSessionData.getCreatedMillis());
        super.save(mongoSession);
        log.info("Secondary Session is created in MONGO");
    }
}