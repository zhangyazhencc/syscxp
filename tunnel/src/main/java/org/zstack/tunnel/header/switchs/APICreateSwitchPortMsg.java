package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateSwitchPortMsg extends APIMessage {

    @APIParam(nonempty = true,maxLength = 32)
    private String switchUuid;

    @APIParam(nonempty = true,maxLength = 128)
    private String portName;

    @APIParam(nonempty = true,validValues = {"ACCESSIN", "MONITOR","ECP","VPN"})
    private SwitchPortLabel label;

    @APIParam(nonempty = true)
    private Integer reuse;

    @APIParam(nonempty = true)
    private Integer autoAlloc;

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

    public Integer getAutoAlloc() {
        return autoAlloc;
    }

    public void setAutoAlloc(Integer autoAlloc) {
        this.autoAlloc = autoAlloc;
    }
}
