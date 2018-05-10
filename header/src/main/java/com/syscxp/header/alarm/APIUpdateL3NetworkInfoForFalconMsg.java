package com.syscxp.header.alarm;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
//@SuppressCredentialCheck
public class APIUpdateL3NetworkInfoForFalconMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String l3NetworkUuid;

    @APIParam
    private String monitorUuid;

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getMonitorUuid() {
        return monitorUuid;
    }

    public void setMonitorUuid(String monitorUuid) {
        this.monitorUuid = monitorUuid;
    }
}
