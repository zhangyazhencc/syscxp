package com.syscxp.tunnel.header.endpoint;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/1
 */
@StaticMetamodel(InnerConnectedEndpointVO.class)
public class InnerConnectedEndpointVO_ {
    public static volatile SingularAttribute<InnerConnectedEndpointVO, String> uuid;
    public static volatile SingularAttribute<InnerConnectedEndpointVO, String> endpointUuid;
    public static volatile SingularAttribute<InnerConnectedEndpointVO, String> connectedEndpointUuid;
    public static volatile SingularAttribute<InnerConnectedEndpointVO, String> name;
    public static volatile SingularAttribute<InnerConnectedEndpointVO, Timestamp> createDate;
    public static volatile SingularAttribute<InnerConnectedEndpointVO, Timestamp> lastOpDate;
}
