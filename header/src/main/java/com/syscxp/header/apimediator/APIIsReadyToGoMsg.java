package com.syscxp.header.apimediator;

import com.syscxp.header.message.APISyncCallMessage;

public class APIIsReadyToGoMsg extends APISyncCallMessage {
    private String managementNodeId;

    public String getManagementNodeId() {
        return managementNodeId;
    }

    public void setManagementNodeId(String managementNodeId) {
        this.managementNodeId = managementNodeId;
    }

    public APIIsReadyToGoMsg() {
    }
 
    public static APIIsReadyToGoMsg __example__() {
        APIIsReadyToGoMsg msg = new APIIsReadyToGoMsg();


        return msg;
    }

}
