package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.vo.EO;

import javax.persistence.*;

/**
 * Create by DCY on 2018/1/9
 */
@Entity
@Table
@EO(EOClazz = EdgeLineEO.class)
public class EdgeLineVO extends EdgeLineAO{

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="interfaceUuid", insertable=false, updatable=false)
    private InterfaceVO interfaceVO;

    public InterfaceVO getInterfaceVO() {
        return interfaceVO;
    }

    public void setInterfaceVO(InterfaceVO interfaceVO) {
        this.interfaceVO = interfaceVO;
    }
}
