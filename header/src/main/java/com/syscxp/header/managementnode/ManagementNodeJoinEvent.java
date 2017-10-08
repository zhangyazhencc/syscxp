package com.syscxp.header.managementnode;

import com.syscxp.header.message.LocalEvent;

public class ManagementNodeJoinEvent extends LocalEvent {
    private String nodeId;

    public ManagementNodeJoinEvent() {
    }

    public ManagementNodeJoinEvent(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getSubCategory() {
        return ManagementNodeConstant.MANAGEMENT_NODE_EVENT;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
