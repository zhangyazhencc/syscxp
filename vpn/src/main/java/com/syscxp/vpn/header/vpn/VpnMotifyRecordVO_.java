package com.syscxp.vpn.header.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnMotifyRecordVO.class)
public class VpnMotifyRecordVO_ {

    public static volatile SingularAttribute<VpnVO, String> uuid;
    public static volatile SingularAttribute<VpnVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnVO, String> opAccountUuid;
    public static volatile SingularAttribute<VpnVO, MotifyType> motifyType;
    public static volatile SingularAttribute<VpnVO, Timestamp> createDate;
}
