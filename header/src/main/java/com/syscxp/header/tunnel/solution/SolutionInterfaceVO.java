package com.syscxp.header.tunnel.solution;

import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.tunnel.PortOfferingVO;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionInterfaceVO extends SolutionBaseVO{

    @Column
    private String endpointUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuid", insertable = false, updatable = false)
    private EndpointVO endpointVO;

    @Column
    private String portOfferingUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portOfferingUuid", insertable = false, updatable = false)
    private PortOfferingVO portOfferingVO;

    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }

    public PortOfferingVO getPortOfferingVO() {
        return portOfferingVO;
    }

    public void setPortOfferingVO(PortOfferingVO portOfferingVO) {
        this.portOfferingVO = portOfferingVO;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getPortOfferingUuid() {
        return portOfferingUuid;
    }

    public void setPortOfferingUuid(String portOfferingUuid) {
        this.portOfferingUuid = portOfferingUuid;
    }
}
