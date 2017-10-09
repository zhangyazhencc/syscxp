package com.syscxp.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/9
 */
@StaticMetamodel(InterfaceMotifyRecordVO.class)
public class InterfaceMotifyRecordVO_ {
    public static volatile SingularAttribute<InterfaceMotifyRecordVO, String> uuid;
    public static volatile SingularAttribute<InterfaceMotifyRecordVO, String> interfaceUuid;
    public static volatile SingularAttribute<InterfaceMotifyRecordVO, String> opAccountUuid;
    public static volatile SingularAttribute<InterfaceMotifyRecordVO, MotifyType> motifyType;
    public static volatile SingularAttribute<InterfaceMotifyRecordVO, Timestamp> createDate;
}
