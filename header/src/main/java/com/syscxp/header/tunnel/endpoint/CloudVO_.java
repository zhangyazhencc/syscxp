package com.syscxp.header.tunnel.endpoint;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(CloudVO.class)
public class CloudVO_ {
    public static volatile SingularAttribute<EndpointAO, String> uuid;
    public static volatile SingularAttribute<EndpointAO, String> name;
    public static volatile SingularAttribute<EndpointAO, String> description;
    public static volatile SingularAttribute<EndpointAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<EndpointAO, Timestamp> createDate;
}
