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
    @JoinColumn(name="netWorkUuid", insertable=false, updatable=false)
    private NetWorkVO netWorkVO;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="interfaceAUuid", insertable=false, updatable=false)
    private InterfaceVO interfaceAVO;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="interfaceZUuid", insertable=false, updatable=false)
    private InterfaceVO interfaceZVO;

    public NetWorkVO getNetWorkVO() {
        return netWorkVO;
    }

    public void setNetWorkVO(NetWorkVO netWorkVO) {
        this.netWorkVO = netWorkVO;
    }

    public InterfaceVO getInterfaceAVO() {
        return interfaceAVO;
    }

    public void setInterfaceAVO(InterfaceVO interfaceAVO) {
        this.interfaceAVO = interfaceAVO;
    }

    public InterfaceVO getInterfaceZVO() {
        return interfaceZVO;
    }

    public void setInterfaceZVO(InterfaceVO interfaceZVO) {
        this.interfaceZVO = interfaceZVO;
    }
}
