package org.zstack.tunnel.header.node;

import org.zstack.header.vo.EO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-08-21
 */
@Entity
@Table
@EO(EOClazz = NodeEO.class)
public class NodeVO extends NodeAO {

}
