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

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="networkUuid", insertable=false, updatable=false)
    private NetworkVO networkVO;

    @OneToMany
    @JoinColumn(name = "tunnelUuid", insertable = false, updatable = false)
    private List<TunnelInterfaceVO> tunnelInterfaceVO = new ArrayList<TunnelInterfaceVO>();

    public NetworkVO getNetworkVO() {
        return networkVO;
    }

    public void setNetworkVO(NetworkVO networkVO) {
        this.networkVO = networkVO;
    }

    public List<TunnelInterfaceVO> getTunnelInterfaceVO() {
        return tunnelInterfaceVO;
    }

    public void setTunnelInterfaceVO(List<TunnelInterfaceVO> tunnelInterfaceVO) {
        this.tunnelInterfaceVO = tunnelInterfaceVO;
    }
}
