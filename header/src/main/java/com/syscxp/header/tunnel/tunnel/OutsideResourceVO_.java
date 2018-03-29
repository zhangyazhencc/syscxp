package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/3/28
 */
@StaticMetamodel(OutsideResourceVO.class)
public class OutsideResourceVO_ {

    public static volatile SingularAttribute<OutsideResourceVO, String> uuid;
    public static volatile SingularAttribute<OutsideResourceVO, String> resourceType;
    public static volatile SingularAttribute<OutsideResourceVO, String> resourceUuid;
    public static volatile SingularAttribute<OutsideResourceVO, Timestamp> createDate;
    public static volatile SingularAttribute<OutsideResourceVO, Timestamp> lastOpDate;
}
