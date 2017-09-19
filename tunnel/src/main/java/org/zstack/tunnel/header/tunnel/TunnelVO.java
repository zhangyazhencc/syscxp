package org.zstack.tunnel.header.tunnel;

import org.zstack.header.vo.EO;

import javax.persistence.*;

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

    public NetworkVO getNetworkVO() {
        return networkVO;
    }

    public void setNetworkVO(NetworkVO networkVO) {
        this.networkVO = networkVO;
    }
}
