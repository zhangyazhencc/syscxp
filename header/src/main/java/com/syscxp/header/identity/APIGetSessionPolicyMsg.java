package com.syscxp.header.identity;

import com.syscxp.header.message.APIParam;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
@InnerCredentialCheck
public class APIGetSessionPolicyMsg extends APISessionMessage {
    @APIParam(emptyString = false)
    private String sessionUuid;

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }
 
    public static APIGetSessionPolicyMsg __example__() {
        APIGetSessionPolicyMsg msg = new APIGetSessionPolicyMsg();
        msg.setSessionUuid(uuid());
        return msg;
    }

}
