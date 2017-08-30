package org.zstack.tunnel.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-30
 */
@StaticMetamodel(HostAO.class)
public class HostAO_ {
    public static volatile SingularAttribute<HostVO, String> uuid;
    public static volatile SingularAttribute<HostVO, String> name;
    public static volatile SingularAttribute<HostVO, String> code;
    public static volatile SingularAttribute<HostVO, String> ip;
    public static volatile SingularAttribute<HostVO, String> username;
    public static volatile SingularAttribute<HostVO, String> password;
    public static volatile SingularAttribute<HostVO, HostState> state;
    public static volatile SingularAttribute<HostVO, String> status;
    public static volatile SingularAttribute<HostVO, Timestamp> createDate;
    public static volatile SingularAttribute<HostVO, Timestamp> lastOpDate;
}
