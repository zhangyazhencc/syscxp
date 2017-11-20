package com.syscxp.tunnel.solution.header;

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
    private String endpointNameA;
    @Column
    private String endpointNameZ;
    @Column
    private Long bandwidth;

    public String getEndpointNameA() {
        return endpointNameA;
    }

    public void setEndpointNameA(String endpointNameA) {
        this.endpointNameA = endpointNameA;
    }

    public String getEndpointNameZ() {
        return endpointNameZ;
    }

    public void setEndpointNameZ(String endpointNameZ) {
        this.endpointNameZ = endpointNameZ;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
