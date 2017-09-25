package org.zstack.vpn.header.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(VpnKeyVO.class)
public class VpnKeyVO_ {
    public static volatile SingularAttribute<VpnKeyVO, String> uuid;
    public static volatile SingularAttribute<VpnKeyVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnKeyVO, String> vpnKey;
}
