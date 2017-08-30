package org.zstack.tunnel.header.host;

import org.zstack.header.vo.EO;

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
