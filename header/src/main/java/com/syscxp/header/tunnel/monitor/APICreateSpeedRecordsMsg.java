package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-17.
 * @Description: 创建测速纪录.
 */

@SuppressCredentialCheck
public class APICreateSpeedRecordsMsg extends APIMessage {

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;

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

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

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
