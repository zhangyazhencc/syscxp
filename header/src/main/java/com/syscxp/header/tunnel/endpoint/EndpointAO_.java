package com.syscxp.header.tunnel.endpoint;



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
    public static volatile SingularAttribute<EndpointAO, String> cloudType;
    public static volatile SingularAttribute<EndpointAO, EndpointType> endpointType;
    public static volatile SingularAttribute<EndpointAO, EndpointState> state;
    public static volatile SingularAttribute<EndpointAO, EndpointStatus> status;
    public static volatile SingularAttribute<EndpointAO, String> description;
    public static volatile SingularAttribute<EndpointAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<EndpointAO, Timestamp> createDate;
}
