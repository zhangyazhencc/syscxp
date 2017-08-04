package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class SpeedRecordVO_ {


}
