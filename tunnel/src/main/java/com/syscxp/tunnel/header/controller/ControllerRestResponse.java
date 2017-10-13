package com.syscxp.tunnel.header.controller;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-11.
 * @Description: .
 */
public class ControllerRestResponse {
    private boolean success;
    private String code;
    private Msg msg;

    class Msg{
        private String details;

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return "msg{" +
                    "details='" + details + '\'' +
                    '}';
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;

        if("0".equals(this.code))
            this.success = true;
        else
            this.success = false;
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ControllerRestResponse{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg=" + msg +
                '}';
    }
}
