package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.TunnelState;
import com.syscxp.header.tunnel.TunnelStatus;
import com.syscxp.header.tunnel.TunnelConstant;

import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-17
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateTunnelMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(required = false)
    private Integer vsi;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String monitorCidr;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String name;
    @APIParam(required = false)
    private Double distance;
    @APIParam(emptyString = false,required = false,validValues = {"Enabled", "Disabled","Unpaid"})
    private TunnelState state;
    @APIParam(emptyString = false,required = false,validValues = {"Connecting", "Connected","Disconnected"})
    private TunnelStatus status;
    @APIParam(emptyString = false,required = false,validValues = {"Enabled", "Disabled"})
    private TunnelMonitorState monitorState;
    @APIParam(emptyString = false,required = false)
    private String description;
    @APIParam(required = false)
    private Timestamp expireDate;
    @APIParam(required = false)
    private Long bandwidth;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public TunnelState getState() {
        return state;
    }

    public void setState(TunnelState state) {
        this.state = state;
    }

    public TunnelStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelStatus status) {
        this.status = status;
    }

    public TunnelMonitorState getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(TunnelMonitorState monitorState) {
        this.monitorState = monitorState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
