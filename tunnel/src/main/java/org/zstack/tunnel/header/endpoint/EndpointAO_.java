package org.zstack.tunnel.header.endpoint;



import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-23
 */
@StaticMetamodel(EndpointAO.class)
public class EndpointAO_ {
    public static volatile SingularAttribute<EndpointVO, String> uuid;
    public static volatile SingularAttribute<EndpointVO, String> nodeUuid;
    public static volatile SingularAttribute<EndpointVO, String> name;
    public static volatile SingularAttribute<EndpointVO, String> code;
    public static volatile SingularAttribute<EndpointVO, Integer> enabled;
    public static volatile SingularAttribute<EndpointVO, Integer> openToCustomers;
    public static volatile SingularAttribute<EndpointVO, String> description;
    public static volatile SingularAttribute<EndpointVO, EndpointStatus> status;
    public static volatile SingularAttribute<EndpointVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<EndpointVO, Timestamp> createDate;
}
