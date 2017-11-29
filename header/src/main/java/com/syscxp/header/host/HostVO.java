package com.syscxp.header.host;

import com.syscxp.header.vo.BaseResource;
import com.syscxp.header.vo.EO;

import javax.persistence.*;

@Entity
@Table
@EO(EOClazz = HostEO.class)
@BaseResource
@Inheritance(strategy = InheritanceType.JOINED)
public class HostVO extends HostAO {

    public HostVO() {
    }

    protected HostVO(HostVO vo) {
       super(vo);
    }
}

