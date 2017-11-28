package com.syscxp.header.tunnel.solution;

import com.syscxp.header.tunnel.endpoint.EndpointVO;

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
    private String innerEndpointUuid;


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

    public String getInnerEndpointUuid() {
        return innerEndpointUuid;
    }

    public void setInnerEndpointUuid(String innerEndpointUuid) {
        this.innerEndpointUuid = innerEndpointUuid;
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
}
