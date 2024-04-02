package org.springframework.session.data.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.util.StringUtils;

/**
 * SpringRedisSessionConfig provides configuration for Spring sessions stored in Redis.
 * It initializes and configures SpringRedisAsSecondarySession and RedisIndexedSessionRepository.
 * @author Hunny Kalra, Aakash Jain, Shishir Pandey, Hardik Sharma
 */
@Slf4j
public class SpringRedisSessionConfig {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String redisNamespace = RedisIndexedSessionRepository.DEFAULT_NAMESPACE;

    private static SpringRedisAsSecondarySession springRedisAsSecondarySession;

    private RedisSerializer<Object> defaultRedisSerializer;

    private static RedisIndexedSessionRepository redisIndexedSessionRepository;

    private RedisConnectionFactory redisConnectionFactory;

    /**
     * Constructor for SpringRedisSessionConfig.
     */
    public SpringRedisSessionConfig() {}

    /**
     * Retrieves the configured SpringRedisAsSecondarySession.
     * @return The configured SpringRedisAsSecondarySession.
     */
    public SpringRedisAsSecondarySession getSpringRedisOperationsSessionRepository() {
        if (springRedisAsSecondarySession != null) {
            return springRedisAsSecondarySession;
        }

        RedisIndexedSessionRepository sessionRepository = getRedisOperationsSessionRepository();
        springRedisAsSecondarySession =
                new SpringRedisAsSecondarySession(sessionRepository.getSessionRedisOperations());
        return springRedisAsSecondarySession;
    }

    /**
     * Retrieves the configured RedisIndexedSessionRepository.
     * @return The configured RedisIndexedSessionRepository.
     */
    public RedisIndexedSessionRepository getRedisOperationsSessionRepository() {
        if (redisIndexedSessionRepository != null) {
            return redisIndexedSessionRepository;
        }
        RedisTemplate<String, Object> redisTemplate = createRedisTemplate();
        redisIndexedSessionRepository = new RedisIndexedSessionRepository(redisTemplate);
        if (this.defaultRedisSerializer != null) {
            redisIndexedSessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }
        if (StringUtils.hasText(this.redisNamespace)) {
            redisIndexedSessionRepository.setRedisKeyNamespace(this.redisNamespace);
        }
        redisIndexedSessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        redisIndexedSessionRepository.setDatabase(RedisIndexedSessionRepository.DEFAULT_DATABASE);
        return redisIndexedSessionRepository;
    }

    /**
     * Sets the RedisConnectionFactory.
     * @param springSessionRedisConnectionFactory The RedisConnectionFactory object to use for Spring session.
     * @param redisConnectionFactory The RedisConnectionFactory object to use.
     */
    @Autowired
    public void setRedisConnectionFactory(
            @SpringSessionRedisConnectionFactory ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory,
            ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
        RedisConnectionFactory redisConnectionFactoryToUse =
                springSessionRedisConnectionFactory.getIfAvailable();
        if (redisConnectionFactoryToUse == null) {
            redisConnectionFactoryToUse = redisConnectionFactory.getObject();
        }
        this.redisConnectionFactory = redisConnectionFactoryToUse;
    }

    /**
     * Creates a RedisTemplate for interacting with Redis.
     * @return The configured RedisTemplate.
     */
    private RedisTemplate<String, Object> createRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }
        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

