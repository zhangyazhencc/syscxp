package com.syscxp.vpn.header.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnVO.class)
public class VpnVO_ {
    public static volatile SingularAttribute<VpnVO, String> uuid;
    public static volatile SingularAttribute<VpnVO, String> accountUuid;
    public static volatile SingularAttribute<VpnVO, String> hostUuid;
    public static volatile SingularAttribute<VpnVO, String> name;
    public static volatile SingularAttribute<VpnVO, Integer> port;
    public static volatile SingularAttribute<VpnVO, String> description;
    public static volatile SingularAttribute<VpnVO, String> vpnCidr;
    public static volatile SingularAttribute<VpnVO, Long> bandwidth;
    public static volatile SingularAttribute<VpnVO, String> endpointUuid;
    public static volatile SingularAttribute<VpnVO, VpnStatus> status;
    public static volatile SingularAttribute<VpnVO, VpnState> state;
    public static volatile SingularAttribute<VpnVO, Integer> duration;
    public static volatile SingularAttribute<VpnVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<VpnVO, Integer> maxModifies;
    public static volatile SingularAttribute<VpnVO, String> sid;
    public static volatile SingularAttribute<VpnVO, String> certKey;
    public static volatile SingularAttribute<VpnVO, String> memo;
    public static volatile SingularAttribute<VpnVO, Payment> payment;
    public static volatile SingularAttribute<VpnVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnVO, Timestamp> createDate;
}
