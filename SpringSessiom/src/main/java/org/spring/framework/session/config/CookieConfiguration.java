package org.spring.framework.session.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Component
@ConfigurationProperties(prefix = "cookie")
public class CookieConfiguration {

    private boolean sameSiteNoneEnabled;
    private List<String> sameSiteNoneOrigins;

    private final String HEADER_ORIGIN="head_origin";
    public boolean shouldAddSameSiteAsNoneToCookie(HttpServletRequest request) {
        return request != null && request.getHeader(HEADER_ORIGIN) != null && sameSiteNoneEnabled
                && emptyIfNull(sameSiteNoneOrigins).stream()
                        .anyMatch(origin -> request.getHeader(HEADER_ORIGIN).contains(origin));
    }
}
