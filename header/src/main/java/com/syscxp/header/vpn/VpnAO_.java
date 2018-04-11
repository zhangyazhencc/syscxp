package com.syscxp.header.vpn;

import com.syscxp.header.vpn.vpn.Payment;
import com.syscxp.header.vpn.vpn.VpnState;
import com.syscxp.header.vpn.vpn.VpnStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnAO.class)
public class VpnAO_ {
    public static volatile SingularAttribute<VpnAO, String> uuid;
    public static volatile SingularAttribute<VpnAO, String> accountUuid;
    public static volatile SingularAttribute<VpnAO, String> hostUuid;
    public static volatile SingularAttribute<VpnAO, String> vpnCertUuid;
    public static volatile SingularAttribute<VpnAO, String> name;
    public static volatile SingularAttribute<VpnAO, Integer> port;
    public static volatile SingularAttribute<VpnAO, Integer> vlan;
    public static volatile SingularAttribute<VpnAO, String> description;
    public static volatile SingularAttribute<VpnAO, String> sid;
    public static volatile SingularAttribute<VpnAO, String> bandwidthOfferingUuid;
    public static volatile SingularAttribute<VpnAO, String> endpointUuid;
    public static volatile SingularAttribute<VpnAO, String> tunnelUuid;
    public static volatile SingularAttribute<VpnAO, VpnStatus> status;
    public static volatile SingularAttribute<VpnAO, VpnState> state;
    public static volatile SingularAttribute<VpnAO, Integer> duration;
    public static volatile SingularAttribute<VpnAO, Timestamp> expireDate;
    public static volatile SingularAttribute<VpnAO, Integer> maxModifies;
    public static volatile SingularAttribute<VpnAO, String> certKey;
    public static volatile SingularAttribute<VpnAO, String> clientConf;
    public static volatile SingularAttribute<VpnAO, Payment> payment;
    public static volatile SingularAttribute<VpnAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnAO, Timestamp> createDate;
}
