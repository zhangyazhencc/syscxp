package com.syscxp.header.tunnel.solution;

import javax.persistence.*;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionInterfaceVO extends SolutionBaseVO{

    @Column
    private String endpointName;
    @Column
    private String portOfferingName;

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getPortOfferingName() {
        return portOfferingName;
    }

    public void setPortOfferingName(String portOfferingName) {
        this.portOfferingName = portOfferingName;
    }
}
