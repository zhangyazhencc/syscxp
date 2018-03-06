package com.syscxp.header.tunnel.network;


import com.syscxp.header.vo.EO;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table
@EO(EOClazz = L3NetworkEO.class)
public class L3NetworkVO extends L3NetworkAO {

    @OneToMany(targetEntity = L3EndPointVO.class,orphanRemoval=true)
    private Set<L3EndPointVO> l3EndPointVOs;

    public Set<L3EndPointVO> getL3EndPointVOs() {
        return l3EndPointVOs;
    }

    public void setL3EndPointVOs(Set<L3EndPointVO> l3EndPointVOs) {
        l3EndPointVOs = l3EndPointVOs;
    }
}
