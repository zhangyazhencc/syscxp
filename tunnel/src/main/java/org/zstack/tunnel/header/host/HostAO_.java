package org.zstack.tunnel.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-30
 */
@StaticMetamodel(HostAO.class)
public class HostAO_ {
    public static volatile SingularAttribute<HostAO, String> uuid;
    public static volatile SingularAttribute<HostAO, String> name;
    public static volatile SingularAttribute<HostAO, String> code;
    public static volatile SingularAttribute<HostAO, String> ip;
    public static volatile SingularAttribute<HostAO, String> username;
    public static volatile SingularAttribute<HostAO, String> password;
    public static volatile SingularAttribute<HostAO, HostState> state;
    public static volatile SingularAttribute<HostAO, String> status;
    public static volatile SingularAttribute<HostAO, Timestamp> createDate;
    public static volatile SingularAttribute<HostAO, Timestamp> lastOpDate;
}
