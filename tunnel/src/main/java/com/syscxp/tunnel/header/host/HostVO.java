package com.syscxp.tunnel.header.host;

import com.syscxp.header.vo.EO;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-30
 */
@Entity
@Table
@EO(EOClazz = HostEO.class)
public class HostVO extends HostAO {
}
