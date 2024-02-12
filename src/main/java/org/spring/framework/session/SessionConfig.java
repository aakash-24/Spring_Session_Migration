package org.spring.framework.session;

import org.spring.framework.session.policy.HttpSessionMaxAgePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

public abstract class SessionConfig {

    @Value("${session.cookie.max-age.inMins: 43200}")
    protected int sessionCookieMaxAge;

    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;

    protected ClassLoader classLoader;

    @Autowired
    protected HttpSessionMaxAgePolicy httpSessionMaxAgePolicy;



}
