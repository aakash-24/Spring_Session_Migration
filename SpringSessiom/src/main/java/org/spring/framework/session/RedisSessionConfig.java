package org.spring.framework.session;

import org.spring.framework.data.redis.RedisOperationsSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.MapSession;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class RedisSessionConfig extends SessionConfig implements ISessionConfig {

    private Integer maxInactiveIntervalInSeconds = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

    private String redisNamespace = RedisOperationsSessionRepository.DEFAULT_NAMESPACE;

    private RedisFlushMode redisFlushMode = RedisFlushMode.ON_SAVE;

    private RedisConnectionFactory redisConnectionFactory;

    private RedisSerializer<Object> defaultRedisSerializer;

    private static RedisOperationsSessionRepository redisOperationsSessionRepository;
    
    private static RedisOperationsSessionRepository redisOperationsSessionRepository;
    

    public RedisSessionConfig() {
    }

    public RedisOperationsSessionRepository getRedisSessionRepository() {
        if (redisOperationsSessionRepository != null) {
            return redisOperationsSessionRepository;
        }
        org.springframework.session.data.redis.RedisOperationsSessionRepository sessionRepository = getRedisOperationsSessionRepository();
        redisOperationsSessionRepository =
                new RedisOperationsSessionRepository(sessionRepository.getSessionRedisOperations(),
                        httpSessionMaxAgePolicy);
        return redisOperationsSessionRepository;
    }


    public org.springframework.session.data.redis.RedisOperationsSessionRepository getRedisOperationsSessionRepository() {
        if (redisOperationsSessionRepository != null) {
            return redisOperationsSessionRepository;
        }
        RedisTemplate<Object, Object> redisTemplate = createRedisTemplate();
        redisOperationsSessionRepository = new RedisOperationsSessionRepository(redisTemplate);
        redisOperationsSessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        redisOperationsSessionRepository.setDefaultMaxInactiveInterval(sessionCookieMaxAge * 60);
        if (this.defaultRedisSerializer != null) {
            redisOperationsSessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }
        redisOperationsSessionRepository.setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        if (StringUtils.hasText(this.redisNamespace)) {
            redisOperationsSessionRepository.setRedisKeyNamespace(this.redisNamespace);
        }
        redisOperationsSessionRepository.setRedisFlushMode(this.redisFlushMode);
        int database = resolveDatabase();
        redisOperationsSessionRepository.setDatabase(database);
        return redisOperationsSessionRepository;
    }


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

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private RedisTemplate<Object, Object> createRedisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }
        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.setBeanClassLoader(this.classLoader);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private int resolveDatabase() {
        if (ClassUtils.isPresent("io.lettuce.core.RedisClient", null)
                && this.redisConnectionFactory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        if (ClassUtils.isPresent("redis.clients.jedis.Jedis", null)
                && this.redisConnectionFactory instanceof JedisConnectionFactory) {
            return ((JedisConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        return org.springframework.session.data.redis.RedisOperationsSessionRepository.DEFAULT_DATABASE;
    }
}
