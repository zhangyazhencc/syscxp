package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AgentNetworkTypeVO.class)
public class AgentNetworkTypeVO_ {
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> uuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> networkTypeUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> agentUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> ip;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> vxlanPort;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> status;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> gatewayIp;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> gatewayMac;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> physicalPort;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Timestamp> createDate;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Timestamp> lastOpDate;
}
