package org.zstack.header.billing;

import org.zstack.header.message.APIParam;

public class APICreateRenewOrderMsg  extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private String renewUuid;

    public String getRenewUuid() {
        return renewUuid;
    }

    public void setRenewUuid(String renewUuid) {
        this.renewUuid = renewUuid;
    }
}
