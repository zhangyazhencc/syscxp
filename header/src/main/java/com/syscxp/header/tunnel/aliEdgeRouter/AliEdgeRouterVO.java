package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.tunnel.TunnelEO;
import com.syscxp.header.vo.EO;

import javax.persistence.*;

@Entity
@Table
@EO(EOClazz = AliEdgeRouterEO.class)
public class AliEdgeRouterVO extends AliEdgeRouterAO{

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tunnelUuid", insertable = false, updatable = false)
    private TunnelEO tunnelEO;

    public TunnelEO getTunnelEO() {
        return tunnelEO;
    }

    public void setTunnelEO(TunnelEO tunnelEO) {
        this.tunnelEO = tunnelEO;
    }
}
