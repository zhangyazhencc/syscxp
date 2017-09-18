package org.zstack.vpn.header.host;

import org.zstack.vpn.manage.HostState;
import org.zstack.vpn.manage.HostStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnHostVO.class)
public class VpnHostVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> name;
    public static volatile SingularAttribute<VpnHostVO, String> description;
    public static volatile SingularAttribute<VpnHostVO, String> endpoint;
    public static volatile SingularAttribute<VpnHostVO, String> publicIface;
    public static volatile SingularAttribute<VpnHostVO, String> publicIp;
    public static volatile SingularAttribute<VpnHostVO, String> tunnelIface;
    public static volatile SingularAttribute<VpnHostVO, HostState> state;
    public static volatile SingularAttribute<VpnHostVO, HostStatus> status;
    public static volatile SingularAttribute<VpnHostVO, String> manageIp;
    public static volatile SingularAttribute<VpnHostVO, String> sshPort;
    public static volatile SingularAttribute<VpnHostVO, String> username;
    public static volatile SingularAttribute<VpnHostVO, String> password;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
