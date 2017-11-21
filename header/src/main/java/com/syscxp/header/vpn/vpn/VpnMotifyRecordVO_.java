package com.syscxp.header.vpn.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnMotifyRecordVO.class)
public class VpnMotifyRecordVO_ {

    public static volatile SingularAttribute<VpnMotifyRecordVO, String> uuid;
    public static volatile SingularAttribute<VpnMotifyRecordVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnMotifyRecordVO, String> opAccountUuid;
    public static volatile SingularAttribute<VpnMotifyRecordVO, MotifyType> motifyType;
    public static volatile SingularAttribute<VpnMotifyRecordVO, Timestamp> createDate;
}
