package org.zstack.header.vpn;

import org.springframework.http.HttpStatus;
import org.zstack.header.agent.AgentResponse;
import org.zstack.header.rest.RestAPIState;

/**
 */
public class VpnAgentResponse {
    private HttpStatus statusCode;

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

}
