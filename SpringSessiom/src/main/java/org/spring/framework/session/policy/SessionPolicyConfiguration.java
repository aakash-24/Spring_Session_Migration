package org.spring.framework.session.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Configuration
public class SessionPolicyConfiguration {

    @Value("${Session.cookie.max-age.inMins: 43200}")
    private int sessionCookieMaxAge;

    @Bean
    @ConditionalOnProperty(name = "Session.cookie.max-age.policyEnabled", havingValue = "false")
    public HttpSessionMaxAgePolicy sessionMaxAgePolicy() {
        int applicableMaxInactiveInterval = sessionCookieMaxAge * 60;

        return new HttpSessionMaxAgePolicy() {
            @Override
            public Integer getApplicableMaxInactiveInterval(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
                return null;
            }

            @Override
            public Integer getApplicableMaxInactiveInterval(HttpServletRequest request,
                    HttpServletResponse response) {
                log.debug("applicableMaxInactiveInterval for this session is {} seconds.",
                        applicableMaxInactiveInterval);
                return applicableMaxInactiveInterval;
            }

            @Override
            public Integer getCartPreservationTime(HttpServletRequest request, HttpServletResponse response,
                                                   String cartPreservationTime) {
                Integer timeInSeconds = null;
                if (Objects.nonNull(cartPreservationTime)) {
                    timeInSeconds = Integer.parseInt(cartPreservationTime) * 60;
                }
                log.debug("cartPreservationTime for this cart session is {} seconds: ", timeInSeconds);
                return timeInSeconds;
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "Session.cookie.max-age.policyEnabled", havingValue = "true",
            matchIfMissing = true)
    public HttpSessionMaxAgePolicy requestTypeAwareSessionMaxAgePolicy() {
        return new RequestTypeAwareSessionMaxAgePolicy(sessionCookieMaxAge * 60);
    }
}
