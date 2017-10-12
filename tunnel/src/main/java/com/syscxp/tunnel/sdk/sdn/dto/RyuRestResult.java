package com.syscxp.tunnel.sdk.sdn.dto;

import java.util.Arrays;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-11.
 * @Description: .
 */
public class RyuRestResult {
    private Boolean success;
    private Error error;

    class Error{
        private String code;
        private String description;
        private String details;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return "Error{" +
                    ", code='" + code + '\'' +
                    ", description='" + description + '\'' +
                    ", details='" + details + '\'' +
                    '}';
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "RyuRestResult{" +
                "success=" + success +
                ", error=" + error.toString() +
                '}';
    }
}
