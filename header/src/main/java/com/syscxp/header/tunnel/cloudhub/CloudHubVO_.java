package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(CloudHubVO.class)
public class CloudHubVO_ {

    public static volatile SingularAttribute<CloudHubVO, String> uuid;
    public static volatile SingularAttribute<CloudHubVO, Long> number;
    public static volatile SingularAttribute<CloudHubVO, String> name;
    public static volatile SingularAttribute<CloudHubVO, String> description;
    public static volatile SingularAttribute<CloudHubVO, String> accountUuid;
    public static volatile SingularAttribute<CloudHubVO, String> interfaceUuid;
    public static volatile SingularAttribute<CloudHubVO, String> endpointUuid;
    public static volatile SingularAttribute<CloudHubVO, String> cloudHubOfferingUuid;
    public static volatile SingularAttribute<CloudHubVO, Long> bandwidth;
    public static volatile SingularAttribute<CloudHubVO, Integer> tunnelNumber;
    public static volatile SingularAttribute<CloudHubVO, Integer> duration;
    public static volatile SingularAttribute<CloudHubVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<CloudHubVO, Integer> maxModifies;
    public static volatile SingularAttribute<CloudHubVO, Timestamp> expireDate;
    public static volatile SingularAttribute<CloudHubVO, Timestamp> createDate;
    public static volatile SingularAttribute<CloudHubVO, Timestamp> lastOpDate;

}
