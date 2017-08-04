package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AgentVO.class)
public class AgentVO_ {
    public static volatile SingularAttribute<AgentVO, String> uuid;
    public static volatile SingularAttribute<AgentVO, String> endpointUuid;
    public static volatile SingularAttribute<AgentVO, String> switchUuid;
    public static volatile SingularAttribute<AgentVO, String> code;
    public static volatile SingularAttribute<AgentVO, String> ip;
    public static volatile SingularAttribute<AgentVO, String> status;
    public static volatile SingularAttribute<AgentVO, Integer> enabled;
    public static volatile SingularAttribute<AgentVO, Timestamp> createDate;
    public static volatile SingularAttribute<AgentVO, Timestamp> lastOpDate;
}
