package org.zstack.vpn.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(HostInterfaceVO.class)
public class HostInterfaceVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> name;
    public static volatile SingularAttribute<VpnHostVO, String> hostUuid;
    public static volatile SingularAttribute<VpnHostVO, String> endpointUuid;
    public static volatile SingularAttribute<VpnHostVO, String> interfaceUuid;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
