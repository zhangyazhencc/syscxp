package com.syscxp.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/10
 */
@StaticMetamodel(TunnelMotifyRecordVO.class)
public class TunnelMotifyRecordVO_ {
    public static volatile SingularAttribute<TunnelMotifyRecordVO, String> uuid;
    public static volatile SingularAttribute<TunnelMotifyRecordVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelMotifyRecordVO, String> opAccountUuid;
    public static volatile SingularAttribute<TunnelMotifyRecordVO, MotifyType> motifyType;
    public static volatile SingularAttribute<TunnelMotifyRecordVO, Timestamp> createDate;
}
