package com.syscxp.tunnel.header.node;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/1
 */
@StaticMetamodel(ZoneVO.class)
public class ZoneVO_ {

    public static volatile SingularAttribute<ZoneVO, String> uuid;
    public static volatile SingularAttribute<ZoneVO, String> name;
    public static volatile SingularAttribute<ZoneVO, Timestamp> createDate;
    public static volatile SingularAttribute<ZoneVO, Timestamp> lastOpDate;
}
