package com.syscxp.tunnel.sdk.sdn.vo;

import java.util.Arrays;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-11.
 * @Description: .
 */
public class RestResult {
    private Boolean success;
    private Error error;

    class Error{
        private String[] causes;
        private String code;
        private String description;
        private String details;

        public String[] getCauses() {
            return causes;
        }

        public void setCauses(String[] causes) {
            this.causes = causes;
        }

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
                    "causes=" + Arrays.toString(causes) +
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
        return "RestResult{" +
                "success=" + success +
                ", error=" + error.toString() +
                '}';
    }
}
