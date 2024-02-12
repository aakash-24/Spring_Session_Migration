package org.spring.framework.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionData {

    private String id;
    private Duration maxInactiveInterval;
    private Map<String, Object> attributes;
    private long createdMillis;
}
