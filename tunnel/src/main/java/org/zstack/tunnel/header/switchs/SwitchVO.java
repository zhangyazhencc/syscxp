package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.EO;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-24
 */
@Entity
@Table
@EO(EOClazz = SwitchEO.class)
public class SwitchVO extends SwitchAO {
}
