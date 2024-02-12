package org.spring.framework.session.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.spring.framework.session.policy.HttpSessionMaxAgePolicy;

import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;


import java.util.List;

public class CookieHttpSessionIdResolver implements HttpSessionIdResolver {

    private static final String WRITTEN_SESSION_ID_ATTR =
            org.springframework.session.web.http.CookieHttpSessionIdResolver.class.getName().concat(".WRITTEN_SESSION_ID_ATTR");

    private org.springframework.session.web.http.CookieHttpSessionIdResolver delegateResolver;
    private HttpSessionMaxAgePolicy httpSessionMaxAgePolicy;
    private CookieSerializer cookieSerializer = new DefaultCookieSerializer();

    public CookieHttpSessionIdResolver(org.springframework.session.web.http.CookieHttpSessionIdResolver delegateResolver,
                                       HttpSessionMaxAgePolicy httpSessionMaxAgePolicy) {
        this.delegateResolver = delegateResolver;
        this.httpSessionMaxAgePolicy = httpSessionMaxAgePolicy;
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        return delegateResolver.resolveSessionIds(request);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (sessionId.equals(request.getAttribute(WRITTEN_SESSION_ID_ATTR))) {
            return;
        }
        request.setAttribute(WRITTEN_SESSION_ID_ATTR, sessionId);
        CookieSerializer.CookieValue cookieValue =
                new CookieSerializer.CookieValue(request, response, sessionId);
        cookieValue.setCookieMaxAge(httpSessionMaxAgePolicy.getApplicableMaxInactiveInterval(request, response));
        this.cookieSerializer.writeCookieValue(cookieValue);

        delegateResolver.setSessionId(request, response, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        delegateResolver.expireSession(request, response);
    }

    public void setCookieSerializer(CookieSerializer cookieSerializer) {
        if (cookieSerializer == null) {
            throw new IllegalArgumentException("cookieSerializer cannot be null");
        }
        this.cookieSerializer = cookieSerializer;
    }

}
