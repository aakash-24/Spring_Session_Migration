package org.framework.data;

import lombok.*;
import java.time.Duration;
import java.util.Map;

/**
 * SpringSessionData is a data class representing session data.
 * @author Aakash Jain
 */
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpringSessionData {

    /**
     * The ID of the session.
     */
    private String id;

    /**
     * The maximum inactive interval for the session.
     */
    private Duration maxInactiveInterval;

    /**
     * The attributes associated with the session.
     */
    private Map<String, Object> attributes;

    /**
     * The time at which the session was created, in milliseconds since the epoch.
     */
    private long createdMillis;
}
