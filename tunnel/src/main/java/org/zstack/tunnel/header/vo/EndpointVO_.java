package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(EndpointVO.class)
public class EndpointVO_ {
    public static volatile SingularAttribute<EndpointVO, String> uuid;
    public static volatile SingularAttribute<EndpointVO, String> nodeUuid;
    public static volatile SingularAttribute<EndpointVO, String> name;
    public static volatile SingularAttribute<EndpointVO, String> code;
    public static volatile SingularAttribute<EndpointVO, Integer> enabled;
    public static volatile SingularAttribute<EndpointVO, String> openToCustomers;
    public static volatile SingularAttribute<EndpointVO, String> status;
    public static volatile SingularAttribute<EndpointVO, String> subType;
    public static volatile SingularAttribute<EndpointVO, Integer> deleted;
    public static volatile SingularAttribute<EndpointVO, Timestamp> description;
    public static volatile SingularAttribute<EndpointVO, Timestamp> lastOpDate;
}
