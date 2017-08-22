package org.zstack.tunnel.header.identity.node;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.EO;

import javax.persistence.*;
import java.sql.Timestamp;
/**
 * Created by DCY on 2017-08-21
 */
@Entity
@Table
@EO(EOClazz = NodeEO.class)
public class NodeVO extends NodeAO {

}
