package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateSwitchVlanMsg extends APIMessage {

    @APIParam(nonempty = true,maxLength = 32)
    private String switchUuid;

    @APIParam(nonempty = true)
    private Integer startVlan;

    @APIParam(nonempty = true)
    private Integer endVlan;

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }
}
