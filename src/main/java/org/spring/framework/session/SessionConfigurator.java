package org.spring.framework.session;

import org.spring.framework.session.config.CookieConfiguration;
import org.spring.framework.session.config.CookieHttpSessionIdResolver;
import org.spring.framework.session.config.CookieSerializer;
import org.spring.framework.session.policy.HttpSessionMaxAgePolicy;
import org.spring.framework.data.redis.SessionRepository;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.Session;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
public class SessionConfigurator extends SpringHttpSessionConfiguration {

    @Autowired
    private HttpSessionMaxAgePolicy httpSessionMaxAgePolicy;

    @Value("${use.secure.cookies: true}")
    private boolean useSecureCookies;

    @Value("${cookie.max-age.inMins: 43200}")
    private int sessionCookieMaxAge;


    @Value("${cookie.max-age.policyEnabled: true}")
    private boolean sessionPolicyEnabled;

    private final String SPRING_SESSION = "spring_session";

    @Autowired
    private CookieConfiguration cookieConfiguration;

    private ServletContext servletContext;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public <S extends Session> SessionRepositoryFilter<? extends Session> springSessionRepositoryFilter(
            org.springframework.session.SessionRepository<S> sessionRepository) {

        SessionRepositoryFilter sessionRepositoryFilter =
                new SessionRepositoryFilter(sessionRepository());
        org.springframework.session.web.http.CookieHttpSessionIdResolver httpSessionIdResolver = new org.springframework.session.web.http.CookieHttpSessionIdResolver();
        httpSessionIdResolver.setCookieSerializer(cookieSerializer());
        sessionRepositoryFilter.setServletContext(this.servletContext);
        if (!sessionPolicyEnabled) {
            sessionRepositoryFilter.setHttpSessionIdResolver(httpSessionIdResolver);
            return sessionRepositoryFilter;
        }

        CookieHttpSessionIdResolver cookieHttpSessionIdResolver =
                new CookieHttpSessionIdResolver(httpSessionIdResolver, httpSessionMaxAgePolicy);
        cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer());
        sessionRepositoryFilter.setHttpSessionIdResolver(cookieHttpSessionIdResolver);
        return sessionRepositoryFilter;
    }

    @Bean
    public org.springframework.session.SessionRepository sessionRepository() {
        SessionRepository sessionRepository = new SessionRepository();
        sessionRepository.setMongoSessionConfig(MongoSessionConfig());
        sessionRepository.setRedisSessionConfig(RedisSessionConfig());
        return sessionRepository;
    }

    @Bean
    public MongoSessionConfig MongoSessionConfig() {
        return new MongoSessionConfig();
    }

    @Bean
    public RedisSessionConfig RedisSessionConfig() {
        return new RedisSessionConfig();
    }



    private org.springframework.session.web.http.CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new CookieSerializer(cookieConfiguration);
        serializer.setCookieName(SPRING_SESSION);
        serializer.setCookiePath("/");
        // so that cookie domain name does not match host and server host is overriden
        serializer.setDomainNamePattern("\\[.*?\\]");
        serializer.setSameSite(null);

        if (useSecureCookies) {
            serializer.setUseHttpOnlyCookie(true);
            serializer.setUseSecureCookie(true);
        }
        int cookieMaxAgeInSeconds = 60 * sessionCookieMaxAge;
        serializer.setCookieMaxAge(cookieMaxAgeInSeconds);
        return serializer;
    }

}
