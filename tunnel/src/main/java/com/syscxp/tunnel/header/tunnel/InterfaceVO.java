package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.vo.EO;
import com.syscxp.tunnel.header.endpoint.EndpointVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-09-08
 */
@Entity
@Table
@EO(EOClazz = InterfaceEO.class)
public class InterfaceVO extends InterfaceAO{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="endpointUuid", insertable=false, updatable=false)
    private EndpointVO endpointVO;


    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }
}
