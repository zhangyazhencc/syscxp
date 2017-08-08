package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SwitchPortVO.class)
public class SwitchPortVO_ {
    public static volatile SingularAttribute<SwitchPortVO, String> uuid;
    public static volatile SingularAttribute<SwitchPortVO, Integer> portNum;
    public static volatile SingularAttribute<SwitchPortVO, String> agentUuid;
    public static volatile SingularAttribute<SwitchPortVO, String> portName;
    public static volatile SingularAttribute<SwitchPortVO, String> label;
    public static volatile SingularAttribute<SwitchPortVO, Integer> vlan;
    public static volatile SingularAttribute<SwitchPortVO, Integer> endVlan;
    public static volatile SingularAttribute<SwitchPortVO, Integer> reuse;
    public static volatile SingularAttribute<SwitchPortVO, Integer> autoAlloc;
    public static volatile SingularAttribute<SwitchPortVO, Integer> enabled;
    public static volatile SingularAttribute<SwitchPortVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchPortVO, Timestamp> lastOpDate;
}
