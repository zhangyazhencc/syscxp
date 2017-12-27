package com.syscxp.sdk;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:04
 * Author: wj
 */
public class ErrorCodeList extends ErrorCode {

    public java.util.List<ErrorCode> causes;
    public void setCauses(java.util.List<ErrorCode> causes) {
        this.causes = causes;
    }
    public java.util.List<ErrorCode> getCauses() {
        return this.causes;
    }

}
