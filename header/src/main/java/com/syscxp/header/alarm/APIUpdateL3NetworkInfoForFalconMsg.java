package com.syscxp.header.alarm;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@InnerCredentialCheck
public class APIUpdateL3NetworkInfoForFalconMsg extends APISyncCallMessage {

    @APIParam
    private List<String> monitorUuids;

    public List<String> getMonitorUuids() {
        return monitorUuids;
    }

    public void setMonitorUuids(List<String> monitorUuids) {
        this.monitorUuids = monitorUuids;
    }
}
