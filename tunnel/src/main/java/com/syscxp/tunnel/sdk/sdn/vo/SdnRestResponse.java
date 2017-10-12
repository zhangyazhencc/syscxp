package com.syscxp.tunnel.sdk.sdn.vo;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-11.
 * @Description: .
 */
public class SdnRestResponse {
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
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SdnRestResponse{" +
                "code='" + code + '\'' +
                ", msg=" + msg +
                '}';
    }
}
