package com.syscxp.header.vpn.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.Map;

@StaticMetamodel(VpnSystemVO.class)
public class VpnSystemVO_ {
    public static volatile SingularAttribute<VpnSystemVO, String> uuid;
    public static volatile SingularAttribute<VpnSystemVO, Map<String, Object>> vpn;
    public static volatile SingularAttribute<VpnSystemVO, Map<String, Object>> system;
    public static volatile SingularAttribute<VpnSystemVO, Map<String, Object>> tap;
    public static volatile SingularAttribute<VpnSystemVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnSystemVO, Timestamp> createDate;
}
