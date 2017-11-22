package com.syscxp.header.tunnel.solution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangwg on 2017/11/20.
 */

@Entity
@Table
public class SolutionVpnVO extends SolutionBaseVO{

    @Column
    private String zoneUuid;
    @Column
    private String endpointUuid;
    @Column
    private long bandwidth;

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
