package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.host.HostEO;
import com.syscxp.tunnel.header.tunnel.TunnelEO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-17.
 * @Description: 创建测速纪录.
 */
public class APICreateSpeedRecordsMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelEO.class)
    private String tunnelUuid;

    @APIParam(emptyString = false,resourceType = HostEO.class)
    private String srcHostUuid;

    @APIParam(emptyString = false,maxLength = 64)
    private String srcMonitorIp;

    @APIParam(emptyString = false,resourceType = HostEO.class)
    private String dstHostUuid;

    @APIParam(emptyString = false,maxLength = 64)
    private String dstMonitorIp;

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

    public String getSrcHostUuid() {
        return srcHostUuid;
    }

    public void setSrcHostUuid(String srcHostUuid) {
        this.srcHostUuid = srcHostUuid;
    }

    public String getSrcMonitorIp() {
        return srcMonitorIp;
    }

    public void setSrcMonitorIp(String srcMonitorIp) {
        this.srcMonitorIp = srcMonitorIp;
    }

    public String getDstHostUuid() {
        return dstHostUuid;
    }

    public void setDstHostUuid(String dstHostUuid) {
        this.dstHostUuid = dstHostUuid;
    }

    public String getDstMonitorIp() {
        return dstMonitorIp;
    }

    public void setDstMonitorIp(String dstMonitorIp) {
        this.dstMonitorIp = dstMonitorIp;
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
}
