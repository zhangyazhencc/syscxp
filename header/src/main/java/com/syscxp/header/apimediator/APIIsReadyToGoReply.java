package com.syscxp.header.apimediator;

import com.syscxp.header.message.APIReply;

public class APIIsReadyToGoReply extends APIReply {
    private String managementNodeId;

    public APIIsReadyToGoReply() {
    }

    public void setManagementNodeId(String managementNodeId) {
        this.managementNodeId = managementNodeId;
    }

    public String getManagementNodeId() {
        return managementNodeId;
    }
 
    public static APIIsReadyToGoReply __example__() {
        APIIsReadyToGoReply reply = new APIIsReadyToGoReply();


        return reply;
    }

}
