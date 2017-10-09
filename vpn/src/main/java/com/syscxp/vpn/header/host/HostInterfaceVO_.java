package com.syscxp.vpn.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(HostInterfaceVO.class)
public class HostInterfaceVO_ {
    public static volatile SingularAttribute<HostInterfaceVO, String> uuid;
    public static volatile SingularAttribute<HostInterfaceVO, String> name;
    public static volatile SingularAttribute<HostInterfaceVO, String> hostUuid;
    public static volatile SingularAttribute<HostInterfaceVO, String> endpointUuid;
    public static volatile SingularAttribute<HostInterfaceVO, String> interfaceUuid;
    public static volatile SingularAttribute<HostInterfaceVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<HostInterfaceVO, Timestamp> createDate;
}
