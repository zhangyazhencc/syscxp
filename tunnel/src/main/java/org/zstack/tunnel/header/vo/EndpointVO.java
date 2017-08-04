package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.Index;

import javax.persistence.*;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class EndpointVO {

    @Id
    @Column
    private String uuid;

    @Column
    @Index
    private String name;



}
