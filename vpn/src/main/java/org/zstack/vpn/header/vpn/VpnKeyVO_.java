package org.zstack.vpn.header.vpn;

import org.zstack.vpn.header.host.VpnHostVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(VpnKeyVO.class)
public class VpnKeyVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnHostVO, String> vpnKey;
}
