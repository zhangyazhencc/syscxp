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



    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuid")
    private EndpointVO endpointVO;

    @Column
    private String zoneUuid;
    /*@Column
    private String endpointUuid;*/
    @Column
    private long bandwidth;

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

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
