package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.tunnel.tunnel.TunnelType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(CloudHubOfferingVO.class)
public class CloudHubOfferingVO_ {

    public static volatile SingularAttribute<CloudHubOfferingVO, String> uuid;
    public static volatile SingularAttribute<CloudHubOfferingVO, String> name;
    public static volatile SingularAttribute<CloudHubOfferingVO, TunnelType> area;
    public static volatile SingularAttribute<CloudHubOfferingVO, Long> number;
    public static volatile SingularAttribute<CloudHubOfferingVO, Long> bandwidth;
    public static volatile SingularAttribute<CloudHubOfferingVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<CloudHubOfferingVO, Timestamp> createDate;

}
