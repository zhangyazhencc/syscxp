package org.zstack.tunnel.header.endpoint;

import org.zstack.header.vo.EO;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-08-23
 */
@Entity
@Table
@EO(EOClazz = EndpointEO.class)
public class EndpointVO extends EndpointAO {
}
