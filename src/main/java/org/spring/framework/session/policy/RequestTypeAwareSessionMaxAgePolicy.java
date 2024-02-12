package org.spring.framework.session.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class RequestTypeAwareSessionMaxAgePolicy implements HttpSessionMaxAgePolicy {

    private static final List<String> TESTABLE_REQUEST_TYPES =
            Arrays.asList("healthCheck", "testScript", "internalEmployee");
    private static final String REQUEST_TYPE = "requestType";
    private static final String TRAFFIC_TYPE = "traffictype";
    private static final String REFERER = "referer";
    private Integer defaultMaxInactiveInterval;

    public RequestTypeAwareSessionMaxAgePolicy(Integer defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    @Override
    public Integer getApplicableMaxInactiveInterval(HttpServletRequest request,
            HttpServletResponse response) {
        int applicableMaxInactiveInterval = defaultMaxInactiveInterval;
        if (isNotRealUserRequest(request)) {
            applicableMaxInactiveInterval = 10 * 60;
        }

        log.debug("applicableMaxInactiveInterval for this session is {} seconds.",
                applicableMaxInactiveInterval);
        return applicableMaxInactiveInterval;
    }

    @Override
    public Integer getCartPreservationTime(HttpServletRequest request, HttpServletResponse response,
                                           String cartPreservationTime) {
        Integer timeInSeconds = null;
        if (isNotRealUserRequest(request)) {
            timeInSeconds = 10 * 60;
        } else if (Objects.nonNull(cartPreservationTime)) {
            timeInSeconds = Integer.parseInt(cartPreservationTime) * 60;
        }
        log.debug("cartPreservationTime for this cart session is {} seconds: ", timeInSeconds);
        return timeInSeconds;
    }

    private boolean isNotRealUserRequest(HttpServletRequest request) {
        String requesTypeValue = HttpUtils.getParameterValue (request, REQUEST_TYPE);
        String trafficType = HttpUtils.getParameterValue(request, TRAFFIC_TYPE);
        if (isTestableRequestType(requesTypeValue) || isTestableRequestType(trafficType)) {
            log.debug("Request URL contains testable requestType={} or traffictype={}", requesTypeValue,
                    trafficType);
            return true;
        }

        String refererHeaderValue = HttpUtils.getHeaderValue(request, REFERER);
        if (StringUtils.isEmpty(refererHeaderValue)) {
            log.debug("Referer header is not present in the request. This can't be testable request");
            return false;
        }

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(refererHeaderValue).build();
        MultiValueMap<String, String> queryParams = builder.getQueryParams();
        if (CollectionUtils.isEmpty(queryParams)) {
            log.debug("No testable queryParams found in the referer header={}", refererHeaderValue);
            return false;
        }

        requesTypeValue = queryParams.getFirst(REQUEST_TYPE);
        trafficType = queryParams.getFirst(trafficType);
        if (isTestableRequestType(requesTypeValue) || isTestableRequestType(trafficType)) {
            log.debug("Referer header contains testable requestType={} or traffictype={}", refererHeaderValue,
                    trafficType);
            return true;
        }

        log.debug("Request URL {} is not a testable request", request.getRequestURL());
        return false;
    }

    private boolean isTestableRequestType(String requestType) {
        return TESTABLE_REQUEST_TYPES.contains(requestType);
    }
}
