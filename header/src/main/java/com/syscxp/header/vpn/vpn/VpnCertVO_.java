package com.syscxp.header.vpn.vpn;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnCertVO.class)
public class VpnCertVO_ {
    public static volatile SingularAttribute<VpnVO, String> uuid;
    public static volatile SingularAttribute<VpnVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnVO, String> caCert;
    public static volatile SingularAttribute<VpnVO, String> clientCert;
    public static volatile SingularAttribute<VpnVO, String> clientKey;
    public static volatile SingularAttribute<VpnVO, String> clientConf;
    public static volatile SingularAttribute<VpnVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnVO, Timestamp> createDate;

}
