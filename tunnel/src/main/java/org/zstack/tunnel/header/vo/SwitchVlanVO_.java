package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SwitchVlanVO.class)
public class SwitchVlanVO_ {
    public static volatile SingularAttribute<SwitchVlanVO, String> uuid;
    public static volatile SingularAttribute<SwitchVlanVO, String> switchtUuid;
    public static volatile SingularAttribute<SwitchVlanVO, Integer> startVlan;
    public static volatile SingularAttribute<SwitchVlanVO, Integer> endVlan;
    public static volatile SingularAttribute<SwitchVlanVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchVlanVO, Timestamp> lastOpDate;
}
