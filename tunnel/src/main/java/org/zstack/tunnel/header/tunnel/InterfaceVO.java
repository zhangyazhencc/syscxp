package org.zstack.tunnel.header.tunnel;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.endpoint.EndpointEO;
import org.zstack.tunnel.header.endpoint.EndpointVO;
import org.zstack.tunnel.header.switchs.SwitchPortVO;

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
