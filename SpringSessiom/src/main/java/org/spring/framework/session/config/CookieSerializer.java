package org.spring.framework.session.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.util.Collection;

@Slf4j
public class CookieSerializer extends DefaultCookieSerializer {

    private final CookieConfiguration cookieConfiguration;

    private final String SPRING_SESSION = "spring_session";

    public CookieSerializer(CookieConfiguration cookieConfiguration) {
        this.cookieConfiguration = cookieConfiguration;
    }

    @Override
    public void writeCookieValue(CookieValue cookieValue) {
        super.writeCookieValue(cookieValue);
        if (cookieConfiguration.shouldAddSameSiteAsNoneToCookie(cookieValue.getRequest())) {
            Collection<String> setCookieHeaders =
                    cookieValue.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
            String springHeader = setCookieHeaders.stream()
                    .filter(header -> header.contains(SPRING_SESSION)).findFirst().orElse(null);
            if (springHeader != null) {
                log.info("[Cookie] Adding same site attribute to {}", SPRING_SESSION);
                setCookieHeaders.remove(springHeader);
                springHeader = springHeader;
                setCookieHeaders.add(springHeader);
                cookieValue.getResponse().setHeader(HttpHeaders.SET_COOKIE,
                        setCookieHeaders.stream().findFirst().orElse(null));
                setCookieHeaders.stream().skip(1).forEach(
                        header -> cookieValue.getResponse().addHeader(HttpHeaders.SET_COOKIE, header));
            }
        }
    }
}
