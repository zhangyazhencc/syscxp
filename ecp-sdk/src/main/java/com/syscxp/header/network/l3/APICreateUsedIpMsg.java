package com.syscxp.header.network.l3;


import com.syscxp.header.message.APISyncCallMessage;

public class APICreateUsedIpMsg extends APISyncCallMessage {
    private String l3NetworkUuid;

    private String requiredIp;

    private String usedFor;

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getRequiredIp() {
        return requiredIp;
    }

    public void setRequiredIp(String requiredIp) {
        this.requiredIp = requiredIp;
    }

    public String getUsedFor() {
        return usedFor;
    }

    public void setUsedFor(String usedFor) {
        this.usedFor = usedFor;
    }
}
