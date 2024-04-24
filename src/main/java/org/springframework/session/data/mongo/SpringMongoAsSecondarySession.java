package org.springframework.session.data.mongo;

import org.framework.data.SpringSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.ISessionSaveAsSecondary;

import java.util.Map;

/**
 * SpringMongoAsSecondarySession extends MongoIndexedSessionRepository and implements ISessionSaveAsSecondaryOperation.
 * This class is responsible for saving session data as a secondary operation in MongoDB.
 * @author Hunny Kalra
 */

@Slf4j
public class SpringMongoAsSecondarySession extends MongoIndexedSessionRepository
        implements ISessionSaveAsSecondary {

    /**
     * Constructor for SpringMongoAsSecondarySession.
     * @param mongoOperations The MongoOperations object to interact with MongoDB.
     */

    public SpringMongoAsSecondarySession(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    /**
     * Saves session data as secondary in MongoDB.
     * @param springSessionData The SpringSessionData object containing session data to be saved.
     */

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.info("Mongo is set to save secondary session");
        MongoSession mongoSession = findById(springSessionData.getId());
        if(mongoSession==null){
            mongoSession = new MongoSession(springSessionData.getId(),
                    springSessionData.getMaxInactiveInterval().getSeconds());
        }
        else{
            mongoSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        }
        Map<String,Object> keys = springSessionData.getAttributes();
        for(Map.Entry<String,Object> key : keys.entrySet()){
            mongoSession.setAttribute(key.getKey(), key.getValue());
        }
        mongoSession.setCreationTime(springSessionData.getCreatedMillis());
        log.info("Secondary Session is created in Mongo");
        super.save(mongoSession);
    }
}