package org.spring.framework.data.mongo;

import com.dt.platform.session.DTSessionData;
import com.dt.platform.session.policy.HttpSessionMaxAgePolicy;
import com.dt.platform.utils.http.HttpUtils;
import org.spring.framework.session.SessionData;
import org.spring.framework.session.policy.HttpSessionMaxAgePolicy;
import org.spring.framework.data.ISessionOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.data.ISessionOperations;
import org.springframework.session.data.mongo.MongoSession;

import java.time.Duration;
import java.util.Map;

/**
 * Description: This is custom version of MongoOperationsSessionRepository. Date 08/06/23-05:40 pm
 *
 * @author aakashjain
 * @since
 */
@Slf4j
public class MongoOperationsSessionRepository extends org.springframework.session.data.mongo.MongoOperationsSessionRepository
        implements ISessionOperations {

    private HttpSessionMaxAgePolicy httpSessionMaxAgePolicy;


    public MongoOperationsSessionRepository(MongoOperations mongoOperations,
                                            HttpSessionMaxAgePolicy httpSessionMaxAgePolicy) {
        super(mongoOperations);
        this.httpSessionMaxAgePolicy = httpSessionMaxAgePolicy;
    }

    @Override
    public MongoSession createSession() {
        log.debug("Going to customize session while creation.");
        MongoSession mongoSession = new MongoSession();
        mongoSession.setMaxInactiveInterval(
                Duration.ofSeconds(httpSessionMaxAgePolicy.getApplicableMaxInactiveInterval(
                        HttpUtils.resolveHttpRequest(), HttpUtils.resolveHttpResponse())));
        log.debug("Session with custom max age policy created.");
        return mongoSession;
    }

    @Override
    public void save(MongoSession mongoSession) {
        log.debug("Session created as primary in mongo");
        log.info("Session created as primary in mongo");
        super.save(mongoSession);
    }

    @Override
    public void saveAsSecondary(SessionData sessionData) {
        log.debug("Going to save session as secondary in mongo");
        MongoSession mongoSession = findById(sessionData.getId());
        if(mongoSession==null){
            mongoSession = new MongoSession(sessionData.getId(),
                    sessionData.getMaxInactiveInterval().getSeconds());
        }else{
          mongoSession.setMaxInactiveInterval(sessionData.getMaxInactiveInterval());
        }
        Map<String,Object> keys = sessionData.getAttributes();
        for(Map.Entry<String,Object> key : keys.entrySet()){
            mongoSession.setAttribute(key.getKey(), key.getValue());
        }
        mongoSession.setCreationTime(sessionData.getCreatedMillis());
        log.debug("Session created as secondary in mongo");
        super.save(mongoSession);
    }
}
