package com.syscxp.header.vpn.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ZoneVO.class)
public class ZoneVO_ {
    public static volatile SingularAttribute<ZoneVO, String> uuid;
    public static volatile SingularAttribute<ZoneVO, String> name;
    public static volatile SingularAttribute<ZoneVO, String> description;
    public static volatile SingularAttribute<ZoneVO, String> province;
    public static volatile SingularAttribute<ZoneVO, String> nodeUuid;
    public static volatile SingularAttribute<ZoneVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<ZoneVO, Timestamp> createDate;
}
