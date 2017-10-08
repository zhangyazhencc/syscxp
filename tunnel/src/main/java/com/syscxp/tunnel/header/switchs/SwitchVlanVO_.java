package com.syscxp.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-29
 */
@StaticMetamodel(SwitchVlanVO.class)
public class SwitchVlanVO_ {
    public static volatile SingularAttribute<SwitchVlanVO, String> uuid;
    public static volatile SingularAttribute<SwitchVlanVO, String> switchUuid;
    public static volatile SingularAttribute<SwitchVlanVO, Integer> startVlan;
    public static volatile SingularAttribute<SwitchVlanVO, Integer> endVlan;
    public static volatile SingularAttribute<SwitchVlanVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchVlanVO, Timestamp> lastOpDate;
}
