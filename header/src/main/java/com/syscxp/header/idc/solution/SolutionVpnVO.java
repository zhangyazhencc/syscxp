package com.syscxp.header.idc.solution;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */

@Entity
@Table
public class SolutionVpnVO extends SolutionBaseVO{


    @Column
    private String endpointUuid;

    @Column
    private String solutionTunnelUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solutionTunnelUuid", insertable = false, updatable = false)
    private SolutionTunnelVO solutionTunnelVO;

    @Column
    private String bandwidthOfferingUuid;

    public String getSolutionTunnelUuid() {
        return solutionTunnelUuid;
    }

    public void setSolutionTunnelUuid(String solutionTunnelUuid) {
        this.solutionTunnelUuid = solutionTunnelUuid;
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

    public SolutionTunnelVO getSolutionTunnelVO() {
        return solutionTunnelVO;
    }

    public void setSolutionTunnelVO(SolutionTunnelVO solutionTunnelVO) {
        this.solutionTunnelVO = solutionTunnelVO;
    }
}
