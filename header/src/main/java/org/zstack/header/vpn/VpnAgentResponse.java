package org.zstack.header.vpn;

import org.springframework.http.HttpStatus;
import org.zstack.header.rest.RestAPIState;

/**
 */
public class VpnAgentResponse {
    private HttpStatus statusCode;
    private RestAPIState state;
    private TaskResult result;

    public TaskResult getResult() {
        return result;
    }

    public void setResult(TaskResult result) {
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

    public static class TaskResult {
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
