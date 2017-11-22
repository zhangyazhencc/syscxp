package com.syscxp.header.tunnel.solution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    private long bandwidth;

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

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
