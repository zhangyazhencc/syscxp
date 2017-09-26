package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Create by DCY on 2017/9/26
 */
public class APICreateMonitorCidrMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String monitorCidr;

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
