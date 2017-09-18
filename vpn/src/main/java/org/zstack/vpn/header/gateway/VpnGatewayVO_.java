package org.zstack.vpn.header.gateway;

import org.zstack.vpn.header.host.VpnHostVO;
import org.zstack.vpn.manage.EntityState;
import org.zstack.vpn.manage.RunningStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnGatewayVO.class)
public class VpnGatewayVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> accountUuid;
    public static volatile SingularAttribute<VpnHostVO, String> hostUuid;
    public static volatile SingularAttribute<VpnHostVO, String> name;
    public static volatile SingularAttribute<VpnHostVO, String> description;
    public static volatile SingularAttribute<VpnHostVO, String> vpnCidr;
    public static volatile SingularAttribute<VpnHostVO, Integer> bandwidth;
    public static volatile SingularAttribute<VpnHostVO, String> endpoint;
    public static volatile SingularAttribute<VpnHostVO, RunningStatus> status;
    public static volatile SingularAttribute<VpnHostVO, EntityState> state;
    public static volatile SingularAttribute<VpnHostVO, Integer> months;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
