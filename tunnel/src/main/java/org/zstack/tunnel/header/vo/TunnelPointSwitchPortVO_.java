package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AgentNetworkTypeVO.class)
public class TunnelPointSwitchPortVO_ {
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> uuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> switchUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> switchPortUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> tunnelPointUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> groupUuid;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Integer> portNum;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> portName;
    public static volatile SingularAttribute<AgentNetworkTypeVO, String> portType;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Integer> vlan;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Integer> innerVlan;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Integer> deleted;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Timestamp> createDate;
    public static volatile SingularAttribute<AgentNetworkTypeVO, Timestamp> lastOpDate;
}
