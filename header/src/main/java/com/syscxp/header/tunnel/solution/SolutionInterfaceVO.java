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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endpointUuid")
    private EndpointVO endpointVO;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portOfferingUuid")
    private PortOfferingVO portOfferingVO;

   /* @Column
    private String endpointUuid;
    @Column
    private String portOfferingUuid;*/

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
}
