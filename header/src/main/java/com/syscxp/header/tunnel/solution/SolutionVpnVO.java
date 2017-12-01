package com.syscxp.header.tunnel.solution;

import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.vpn.host.ZoneVO;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */

@Entity
@Table
public class SolutionVpnVO extends SolutionBaseVO{


    @Column
    private String endpointUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuid", insertable = false, updatable = false)
    private EndpointVO endpointVO;

    @Column
    private String zoneUuid;
    @Column
    private String bandwidthOfferingUuid;

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }
}
