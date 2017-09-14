package org.zstack.tunnel.header.endpoint;



import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-23
 */
@StaticMetamodel(EndpointAO.class)
public class EndpointAO_ {
    public static volatile SingularAttribute<EndpointAO, String> uuid;
    public static volatile SingularAttribute<EndpointAO, String> nodeUuid;
    public static volatile SingularAttribute<EndpointAO, String> name;
    public static volatile SingularAttribute<EndpointAO, String> code;
    public static volatile SingularAttribute<EndpointAO, EndpointType> endpointType;
    public static volatile SingularAttribute<EndpointAO, Integer> enabled;
    public static volatile SingularAttribute<EndpointAO, Integer> openToCustomers;
    public static volatile SingularAttribute<EndpointAO, String> description;
    public static volatile SingularAttribute<EndpointAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<EndpointAO, Timestamp> createDate;
}
