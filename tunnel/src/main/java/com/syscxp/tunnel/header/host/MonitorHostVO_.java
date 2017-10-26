package com.syscxp.tunnel.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import com.syscxp.header.host.HostVO_;

/**
 */
@StaticMetamodel(MonitorHostVO.class)
public class MonitorHostVO_ extends HostVO_ {
    public static volatile SingularAttribute<MonitorHostVO, String> username;
    //cannot get password by using this method,because password is encrypted
    public static volatile SingularAttribute<MonitorHostVO, String> password;
    public static volatile SingularAttribute<MonitorHostVO, String> nodeUuid;
    public static volatile SingularAttribute<MonitorHostVO, Integer> sshPort;
}
