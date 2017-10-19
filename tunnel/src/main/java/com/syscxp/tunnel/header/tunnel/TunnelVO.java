package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.vo.EO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Entity
@Table
@EO(EOClazz = TunnelEO.class)
public class TunnelVO extends TunnelAO{


    @OneToMany
    @JoinColumn(name = "tunnelUuid", insertable = false, updatable = false)
    private List<TunnelInterfaceVO> tunnelInterfaceVOs = new ArrayList<TunnelInterfaceVO>();


    public List<TunnelInterfaceVO> getTunnelInterfaceVOs() {
        return tunnelInterfaceVOs;
    }

    public void setTunnelInterfaceVOs(List<TunnelInterfaceVO> tunnelInterfaceVOs) {
        this.tunnelInterfaceVOs = tunnelInterfaceVOs;
    }
}
