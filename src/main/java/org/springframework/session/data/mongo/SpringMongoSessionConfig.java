package org.springframework.session.data.mongo;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * SpringMongoSessionConfig provides configuration for Spring sessions stored in MongoDB.
 * It initializes and configures SpringMongoAsSecondarySession and MongoIndexedSessionRepository.
 * @author Hunny Kalra
 */
@Slf4j
public class SpringMongoSessionConfig {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String collectionName = "sessions";

    private MongoOperations mongoOperations;

    private SpringMongoAsSecondarySession springMongoAsSecondarySession;

    private MongoIndexedSessionRepository mongoIndexedSessionRepository;

    private final Integer MAX_TIME_INACTIVE_SESSION = 1800;

    /**
     * Constructor for SpringMongoSessionConfig.
     * @param mongoOperations The MongoOperations object to interact with MongoDB.
     */
    public SpringMongoSessionConfig(MongoOperations mongoOperations) {
        this.mongoOperations=mongoOperations;
    }

    /**
     * Setter for the mongoSessionConverter property.
     * @param mongoSessionConverter The AbstractMongoSessionConverter object to convert sessions.
     */
    @Setter
    private AbstractMongoSessionConverter mongoSessionConverter =
            new JdkMongoSessionConverter(Duration.ofSeconds(MAX_TIME_INACTIVE_SESSION));

    /**
     * Retrieves the configured SpringMongoAsSecondarySession.
     * @return The configured SpringMongoAsSecondarySession.
     */
    public SpringMongoAsSecondarySession getSpringMongoOperationsSessionRepository() {
        if (springMongoAsSecondarySession != null) {
            return springMongoAsSecondarySession;
        }
        springMongoAsSecondarySession
                = new SpringMongoAsSecondarySession(mongoOperations);
        setMongoRepositoryParameters(springMongoAsSecondarySession);
        return springMongoAsSecondarySession;
    }

    /**
     * Retrieves the configured MongoIndexedSessionRepository.
     * @return The configured MongoIndexedSessionRepository.
     */
    public MongoIndexedSessionRepository getMongoOperationsSessionRepository() {
        if (mongoIndexedSessionRepository != null) {
            return mongoIndexedSessionRepository;
        }
        mongoIndexedSessionRepository =
                new MongoIndexedSessionRepository(mongoOperations);
        setMongoRepositoryParameters(mongoIndexedSessionRepository);
        return mongoIndexedSessionRepository;
    }

    /**
     * Sets parameters for the MongoIndexedSessionRepository.
     * @param repository The MongoIndexedSessionRepository to set parameters for.
     */
    private void setMongoRepositoryParameters(MongoIndexedSessionRepository repository) {
        repository.setMaxInactiveIntervalInSeconds(MAX_TIME_INACTIVE_SESSION);

        if (this.mongoSessionConverter != null) {
            repository.setMongoSessionConverter(this.mongoSessionConverter);
        } else {
            repository.setMongoSessionConverter(mongoSessionConverter);
        }

        if (StringUtils.hasText(this.collectionName)) {
            repository.setCollectionName(this.collectionName);
        }
        repository.setApplicationEventPublisher(this.applicationEventPublisher);
    }
}

