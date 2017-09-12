package org.zstack.tunnel.header.monitor;

import org.zstack.header.vo.EO;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@Entity
@Table
@EO(EOClazz = HostSwitchMonitorEO.class)
public class HostSwitchMonitorVO extends HostSwitchMonitorAO{

}
