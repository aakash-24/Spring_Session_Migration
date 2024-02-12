package org.spring.framework.session.policy;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HttpSessionMaxAgePolicy {

    Integer getApplicableMaxInactiveInterval(HttpServletRequest request, HttpServletResponse response);

    Integer getApplicableMaxInactiveInterval(javax.servlet.http.HttpServletRequest request,
                                             javax.servlet.http.HttpServletResponse response);

    Integer getCartPreservationTime(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
                                    String cartPreservationTime);
}
