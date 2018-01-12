package com.syscxp.header.tunnel.solution;

import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionTunnelVO extends SolutionBaseVO{

    @Column
    private String endpointUuidA;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuidA", insertable = false, updatable = false)
    private EndpointVO endpointVOA;

    @Column
    private String endpointUuidZ;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuidZ", insertable = false, updatable = false)
    private EndpointVO endpointVOZ;

    @Column
    private String bandwidthOfferingUuid;

    @Column
    private String innerConnectedEndpointUuid;

    @Column
    private boolean isShareA;

    @Column
    private boolean isShareZ;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "innerConnectedEndpointUuid", insertable = false, updatable = false)
    private InnerConnectedEndpointVO innerConnectedEndpointVO;

    public EndpointVO getEndpointVOA() {
        return endpointVOA;
    }

    public void setEndpointVOA(EndpointVO endpointVOA) {
        this.endpointVOA = endpointVOA;
    }

    public EndpointVO getEndpointVOZ() {
        return endpointVOZ;
    }

    public void setEndpointVOZ(EndpointVO endpointVOZ) {
        this.endpointVOZ = endpointVOZ;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getInnerConnectedEndpointUuid() {
        return innerConnectedEndpointUuid;
    }

    public void setInnerConnectedEndpointUuid(String innerConnectedEndpointUuid) {
        this.innerConnectedEndpointUuid = innerConnectedEndpointUuid;
    }

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

    public InnerConnectedEndpointVO getInnerConnectedEndpointVO() {
        return innerConnectedEndpointVO;
    }

    public void setInnerConnectedEndpointVO(InnerConnectedEndpointVO innerConnectedEndpointVO) {
        this.innerConnectedEndpointVO = innerConnectedEndpointVO;
    }

    public boolean isShareA() {
        return isShareA;
    }

    public void setShareA(boolean shareA) {
        isShareA = shareA;
    }

    public boolean isShareZ() {
        return isShareZ;
    }

    public void setShareZ(boolean shareZ) {
        isShareZ = shareZ;
    }
}
