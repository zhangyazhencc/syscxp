package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/9/29
 */
@StaticMetamodel(PhysicalSwitchUpLinkRefVO.class)
public class PhysicalSwitchUpLinkRefVO_ {
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, String> uuid;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, String> physicalSwitchUuid;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, String> portName;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, String> uplinkPhysicalSwitchUuid;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, String> uplinkPhysicalSwitchPortName;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<PhysicalSwitchUpLinkRefVO, Timestamp> lastOpDate;
}
