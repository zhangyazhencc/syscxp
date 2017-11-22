package com.syscxp.header.tunnel.solution;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionInterfaceVO extends SolutionBaseVO{

    @Column
    private String endpointUuid;
    @Column
    private String portOfferingUuid;

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
