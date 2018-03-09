package com.syscxp.header.tunnel.network;


import com.syscxp.header.vo.EO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table
@EO(EOClazz = L3NetworkEO.class)
public class L3NetworkVO extends L3NetworkAO {

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "l3NetworkUuid", insertable = false, updatable = false)
    private List<L3EndPointVO> l3EndPointVOS = new ArrayList<L3EndPointVO>();

    public List<L3EndPointVO> getL3EndPointVOS() {
        return l3EndPointVOS;
    }

    public void setL3EndPointVOS(List<L3EndPointVO> l3EndPointVOS) {
        this.l3EndPointVOS = l3EndPointVOS;
    }
}
