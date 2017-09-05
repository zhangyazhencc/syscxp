package org.zstack.header.alipay;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.Map;

@SuppressCredentialCheck
public class APIVerifyNotifyMsg  extends APISyncCallMessage {

    @APIParam
    private Map<String,String> param;

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
