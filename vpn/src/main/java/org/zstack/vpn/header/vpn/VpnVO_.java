package org.zstack.vpn.header.vpn;

import org.zstack.vpn.header.host.VpnHostVO;
import org.zstack.vpn.header.host.HostState;
import org.zstack.vpn.header.host.HostStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnVO.class)
public class VpnVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> accountUuid;
    public static volatile SingularAttribute<VpnHostVO, String> hostUuid;
    public static volatile SingularAttribute<VpnHostVO, String> name;
    public static volatile SingularAttribute<VpnHostVO, Integer> port;
    public static volatile SingularAttribute<VpnHostVO, String> description;
    public static volatile SingularAttribute<VpnHostVO, String> vpnCidr;
    public static volatile SingularAttribute<VpnHostVO, Long> bandwidth;
    public static volatile SingularAttribute<VpnHostVO, String> endpointUuid;
    public static volatile SingularAttribute<VpnHostVO, VpnStatus> status;
    public static volatile SingularAttribute<VpnHostVO, VpnState> state;
    public static volatile SingularAttribute<VpnHostVO, Integer> months;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
