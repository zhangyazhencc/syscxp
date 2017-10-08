package com.syscxp.header.alipay;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.Map;

@SuppressCredentialCheck
public class APIVerifyReturnMsg  extends APISyncCallMessage {

    @APIParam
    private Map<String,String> param;

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
