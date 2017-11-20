package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.switchs.SwitchPortVO;

import java.util.List;

/**
 * Create by DCY on 2017/9/28
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateInterfacePortMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false, maxLength = 32, resourceType = SwitchPortVO.class)
    private String switchPortUuid;
    @APIParam
    private NetworkType networkType;
    @APIParam(required = false)
    private List<InnerVlanSegment> segments;
    @APIParam
    private boolean issue = false;

    public boolean isIssue() {
        return issue;
    }

    public void setIssue(boolean issue) {
        this.issue = issue;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public List<InnerVlanSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<InnerVlanSegment> segments) {
        this.segments = segments;
    }
}

