package com.syscxp.header.tunnel.node;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/1
 */
@StaticMetamodel(ZoneNodeRefVO.class)
public class ZoneNodeRefVO_ {
    public static volatile SingularAttribute<ZoneNodeRefVO, String> uuid;
    public static volatile SingularAttribute<ZoneNodeRefVO, String> nodeUuid;
    public static volatile SingularAttribute<ZoneNodeRefVO, String> zoneUuid;
    public static volatile SingularAttribute<ZoneNodeRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<ZoneNodeRefVO, Timestamp> lastOpDate;
}
