package org.spring.framework.session;

import org.spring.framework.data.mongo.MongoOperationsSessionRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Slf4j
@Service
public class MongoSessionConfig extends SessionConfig implements ISessionConfig {

    @Setter
    private AbstractMongoSessionConverter mongoSessionConverter =
            new JdkMongoSessionConverter(Duration.ofSeconds(sessionCookieMaxAge * 60L));

    private String collectionName = "sessions";


    @Autowired
    private MongoTemplateResolver templateResolver;

    private MongoOperationsSessionRepository mongoOperationsSessionRepository;

    private MongoOperationsSessionRepository mongoOperationsSessionRepository;

    public MongoSessionConfig() {

    }


    public MongoOperationsSessionRepository getDTMongoSessionRepository() {
        if (mongoOperationsSessionRepository != null) {
            return mongoOperationsSessionRepository;
        }
        mongoOperationsSessionRepository = new MongoOperationsSessionRepository(
                templateResolver.mongoTemplate(), httpSessionMaxAgePolicy);
        setMongoRepositoryParameters(mongoOperationsSessionRepository);
        return mongoOperationsSessionRepository;
    }

    public org.springframework.session.data.mongo.MongoOperationsSessionRepository getMongoOperationsSessionRepository() {
        if (mongoOperationsSessionRepository != null) {
            return mongoOperationsSessionRepository;
        }
        mongoOperationsSessionRepository =
                new org.springframework.session.data.mongo.MongoOperationsSessionRepository(templateResolver.mongoTemplate());
        setMongoRepositoryParameters(mongoOperationsSessionRepository);
        return mongoOperationsSessionRepository;
    }

    private void setMongoRepositoryParameters(org.springframework.session.data.mongo.MongoOperationsSessionRepository repository) {
        repository.setMaxInactiveIntervalInSeconds(sessionCookieMaxAge * 60);

        if (this.mongoSessionConverter != null) {
            repository.setMongoSessionConverter(this.mongoSessionConverter);
        } else {
            JdkMongoSessionConverter mongoSessionConverter = new JdkMongoSessionConverter(
                    new SerializingConverter(), new DeserializingConverter(this.classLoader),
                    Duration.ofSeconds(org.springframework.session.data.mongo.MongoOperationsSessionRepository.DEFAULT_INACTIVE_INTERVAL));
            repository.setMongoSessionConverter(mongoSessionConverter);
        }

        if (StringUtils.hasText(this.collectionName)) {
            repository.setCollectionName(this.collectionName);
        }
        repository.setApplicationEventPublisher(applicationEventPublisher);
    }
}
