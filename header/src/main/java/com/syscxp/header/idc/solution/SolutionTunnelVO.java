package com.syscxp.header.idc.solution;

import com.syscxp.header.tunnel.tunnel.TunnelType;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionTunnelVO extends SolutionBaseVO{

    @Column
    private String endpointUuidA;

    @Column
    private String endpointUuidZ;

    @Column
    private String bandwidthOfferingUuid;

    @Column
    private String innerEndpointUuid;

    @Column
    private String interfaceUuidA;

    @Column
    private String interfaceUuidZ;

    @Column
    @Enumerated(EnumType.STRING)
    private TunnelType type;

    public String getEndpointUuidA() {
        return endpointUuidA;
    }

    public void setEndpointUuidA(String endpointUuidA) {
        this.endpointUuidA = endpointUuidA;
    }

    public String getEndpointUuidZ() {
        return endpointUuidZ;
    }

    public void setEndpointUuidZ(String endpointUuidZ) {
        this.endpointUuidZ = endpointUuidZ;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getInnerEndpointUuid() {
        return innerEndpointUuid;
    }

    public void setInnerEndpointUuid(String innerEndpointUuid) {
        this.innerEndpointUuid = innerEndpointUuid;
    }

    public String getInterfaceUuidA() {
        return interfaceUuidA;
    }

    public void setInterfaceUuidA(String interfaceUuidA) {
        this.interfaceUuidA = interfaceUuidA;
    }

    public String getInterfaceUuidZ() {
        return interfaceUuidZ;
    }

    public void setInterfaceUuidZ(String interfaceUuidZ) {
        this.interfaceUuidZ = interfaceUuidZ;
    }

    public TunnelType getType() {
        return type;
    }

    public void setType(TunnelType type) {
        this.type = type;
    }
}
