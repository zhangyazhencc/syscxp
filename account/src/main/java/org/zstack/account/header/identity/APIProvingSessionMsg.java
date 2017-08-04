package org.zstack.account.header.identity;

import org.zstack.header.identity.APISessionMessage;
import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIParam;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressCredentialCheck
public class APIProvingSessionMsg extends APISessionMessage {
    @APIParam
    private String sessionUuid;

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public static APIProvingSessionMsg __example__() {
        APIProvingSessionMsg msg = new APIProvingSessionMsg();
        msg.setSessionUuid(uuid());
        return msg;
    }

}
