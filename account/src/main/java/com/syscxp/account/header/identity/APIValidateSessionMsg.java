package com.syscxp.account.header.identity;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressCredentialCheck
public class APIValidateSessionMsg extends APISessionMessage {
    @APIParam(nonempty = true)
    private String sessionUuid;

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }
 
    public static APIValidateSessionMsg __example__() {
        APIValidateSessionMsg msg = new APIValidateSessionMsg();
        msg.setSessionUuid(uuid());
        return msg;
    }

}
