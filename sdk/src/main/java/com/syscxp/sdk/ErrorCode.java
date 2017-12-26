package com.syscxp.sdk;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:04
 * Author: wj
 */
public class ErrorCode  {

    public String code;
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }

    public String description;
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return this.description;
    }

    public String details;
    public void setDetails(String details) {
        this.details = details;
    }
    public String getDetails() {
        return this.details;
    }

    public String elaboration;
    public void setElaboration(String elaboration) {
        this.elaboration = elaboration;
    }
    public String getElaboration() {
        return this.elaboration;
    }

    public ErrorCode cause;
    public void setCause(ErrorCode cause) {
        this.cause = cause;
    }
    public ErrorCode getCause() {
        return this.cause;
    }

    public java.util.LinkedHashMap opaque;
    public void setOpaque(java.util.LinkedHashMap opaque) {
        this.opaque = opaque;
    }
    public java.util.LinkedHashMap getOpaque() {
        return this.opaque;
    }

}
