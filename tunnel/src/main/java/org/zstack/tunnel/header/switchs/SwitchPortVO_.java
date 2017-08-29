package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-29
 */
@StaticMetamodel(SwitchPortVO.class)
public class SwitchPortVO_ {
    public static volatile SingularAttribute<SwitchPortVO, String> uuid;
    public static volatile SingularAttribute<SwitchPortVO, String> switchUuid;
    public static volatile SingularAttribute<SwitchPortVO, Integer> portNum;
    public static volatile SingularAttribute<SwitchPortVO, String> portName;
    public static volatile SingularAttribute<SwitchPortVO, SwitchPortLabel> label;
    public static volatile SingularAttribute<SwitchPortVO, Integer> reuse;
    public static volatile SingularAttribute<SwitchPortVO, Integer> autoAlloc;
    public static volatile SingularAttribute<SwitchPortVO, Integer> enabled;
    public static volatile SingularAttribute<SwitchPortVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchPortVO, Timestamp> lastOpDate;
}