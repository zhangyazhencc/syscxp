package com.syscxp.header.vpn.vpn;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnCertVO.class)
public class VpnCertVO_ {
    public static volatile SingularAttribute<VpnCertVO, String> uuid;
    public static volatile SingularAttribute<VpnCertVO, String> name;
    public static volatile SingularAttribute<VpnCertVO, String> accountUuid;
    public static volatile SingularAttribute<VpnCertVO, String> caCert;
    public static volatile SingularAttribute<VpnCertVO, String> caKey;
    public static volatile SingularAttribute<VpnCertVO, String> clientCert;
    public static volatile SingularAttribute<VpnCertVO, String> clientKey;
    public static volatile SingularAttribute<VpnCertVO, String> serverCert;
    public static volatile SingularAttribute<VpnCertVO, String> serverKey;
    public static volatile SingularAttribute<VpnCertVO, String> dh1024Pem;
    public static volatile SingularAttribute<VpnCertVO, String> clientConf;
    public static volatile SingularAttribute<VpnCertVO, Integer> version;
    public static volatile SingularAttribute<VpnCertVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnCertVO, Timestamp> createDate;

}
