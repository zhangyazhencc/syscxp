package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.host.HostVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-17.
 * @Description: 创建测速纪录.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY)
public class APICreateSpeedRecordsMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelVO.class)
    private String tunnelUuid;

    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String srcNodeUuid;

    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String dstNodeUuid;

    @APIParam(emptyString = false,validValues = {"TCP","UDP"})
    private ProtocolType protocolType;

    @APIParam(emptyString = false,maxLength = 11)
    private Integer duration;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSrcNodeUuid() {
        return srcNodeUuid;
    }

    public void setSrcNodeUuid(String srcNodeUuid) {
        this.srcNodeUuid = srcNodeUuid;
    }

    public String getDstNodeUuid() {
        return dstNodeUuid;
    }

    public void setDstNodeUuid(String dstNodeUuid) {
        this.dstNodeUuid = dstNodeUuid;
    }
}
