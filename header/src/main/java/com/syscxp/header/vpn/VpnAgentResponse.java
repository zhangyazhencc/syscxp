package com.syscxp.header.vpn;

import com.syscxp.header.rest.RestAPIState;
import org.springframework.http.HttpStatus;

/**
 */
public class VpnAgentResponse {
    private HttpStatus statusCode;
    private RestAPIState state;
    private VpnTaskResult result;

    public VpnTaskResult getResult() {
        return result;
    }

    public void setResult(VpnTaskResult result) {
        this.result = result;
    }

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

    public static class VpnTaskResult {
        private String message;
        private boolean success = true;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

}
