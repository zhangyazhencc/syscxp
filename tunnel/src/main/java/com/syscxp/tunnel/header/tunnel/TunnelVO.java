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


    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "tunnelUuid", insertable = false, updatable = false)
    private List<TunnelSwitchVO> tunnelSwitchVOS = new ArrayList<TunnelSwitchVO>();

    public List<TunnelSwitchVO> getTunnelSwitchVOS() {
        return tunnelSwitchVOS;
    }

    public void setTunnelSwitchVOS(List<TunnelSwitchVO> tunnelSwitchVOS) {
        this.tunnelSwitchVOS = tunnelSwitchVOS;
    }
}
