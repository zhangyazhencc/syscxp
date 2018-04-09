package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.vpn.vpn.Payment;
import com.syscxp.header.vpn.vpn.VpnState;
import com.syscxp.header.vpn.vpn.VpnStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3VpnVO.class)
public class L3VpnVO_ {
    public static volatile SingularAttribute<L3VpnVO, String> uuid;
    public static volatile SingularAttribute<L3VpnVO, String> accountUuid;
    public static volatile SingularAttribute<L3VpnVO, String> hostUuid;
    public static volatile SingularAttribute<L3VpnVO, String> vpnCertUuid;
    public static volatile SingularAttribute<L3VpnVO, String> name;
    public static volatile SingularAttribute<L3VpnVO, Integer> port;
    public static volatile SingularAttribute<L3VpnVO, Integer> vlan;
    public static volatile SingularAttribute<L3VpnVO, String> description;
    public static volatile SingularAttribute<L3VpnVO, String> secretId;
    public static volatile SingularAttribute<L3VpnVO, String> bandwidthOfferingUuid;
    public static volatile SingularAttribute<L3VpnVO, String> l3EndpointUuid;
    public static volatile SingularAttribute<L3VpnVO, String> l3NetworkUuid;
    public static volatile SingularAttribute<L3VpnVO, String> workMode;
    public static volatile SingularAttribute<L3VpnVO, String> startIp;
    public static volatile SingularAttribute<L3VpnVO, String> stopIp;
    public static volatile SingularAttribute<L3VpnVO, String> netmask;
    public static volatile SingularAttribute<L3VpnVO, String> gateway;
    public static volatile SingularAttribute<L3VpnVO, VpnStatus> status;
    public static volatile SingularAttribute<L3VpnVO, VpnState> state;
    public static volatile SingularAttribute<L3VpnVO, Integer> duration;
    public static volatile SingularAttribute<L3VpnVO, Timestamp> expireDate;
    public static volatile SingularAttribute<L3VpnVO, Integer> maxModifies;
    public static volatile SingularAttribute<L3VpnVO, String> secretKey;
    public static volatile SingularAttribute<L3VpnVO, String> clientConf;
    public static volatile SingularAttribute<L3VpnVO, Payment> payment;
    public static volatile SingularAttribute<L3VpnVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3VpnVO, Timestamp> createDate;
}
