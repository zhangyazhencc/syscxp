package com.syscxp.header.tunnel.cloudhub;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(CloudHubTunnelRefVO.class)
public class CloudHubTunnelRefVO_ {

    public static volatile SingularAttribute<CloudHubTunnelRefVO, Long> id;
    public static volatile SingularAttribute<CloudHubTunnelRefVO, String> cloudHubUuid;
    public static volatile SingularAttribute<CloudHubTunnelRefVO, String> tunnelUuid;
    public static volatile SingularAttribute<CloudHubTunnelRefVO, Timestamp> createDate;

}
