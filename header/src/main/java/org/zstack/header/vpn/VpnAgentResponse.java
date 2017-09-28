package org.zstack.header.vpn;

import org.springframework.http.HttpStatus;
import org.zstack.header.agent.AgentResponse;
import org.zstack.header.rest.RestAPIState;

/**
 */
public class VpnAgentResponse extends AgentResponse{
    private HttpStatus statusCode;
    private RestAPIState state;

    public RestAPIState getState() {
        return state;
    }

    public void setState(RestAPIState state) {
        this.state = state;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

}
