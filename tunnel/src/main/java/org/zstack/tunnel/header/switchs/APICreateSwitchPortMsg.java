package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String switchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String portName;

    @APIParam(emptyString = false,validValues = {"RJ45", "SFP_1G","SFG_10G"})
    private SwitchPortType portType;

    @APIParam(emptyString = false,validValues = {"ACCESSIN", "MONITOR","ECP","VPN","CLOUD"})
    private SwitchPortLabel label;

    @APIParam(emptyString = false)
    private Integer isExclusive;

    @APIParam(emptyString = false)
    private Integer reuse;


    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public SwitchPortLabel getLabel() {
        return label;
    }

    public void setLabel(SwitchPortLabel label) {
        this.label = label;
    }

    public Integer getReuse() {
        return reuse;
    }

    public void setReuse(Integer reuse) {
        this.reuse = reuse;
    }

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }

    public Integer getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Integer isExclusive) {
        this.isExclusive = isExclusive;
    }
}
