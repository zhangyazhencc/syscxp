package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-06
 */
@StaticMetamodel(PhysicalSwitchAO.class)
public class PhysicalSwitchAO_ {
    public static volatile SingularAttribute<PhysicalSwitchAO, String> uuid;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> nodeUuid;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> switchModelUuid;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> code;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> name;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> brand;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> owner;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> rack;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> description;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> mIP;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> username;
    public static volatile SingularAttribute<PhysicalSwitchAO, String> password;
    public static volatile SingularAttribute<PhysicalSwitchAO, Timestamp> createDate;
    public static volatile SingularAttribute<PhysicalSwitchAO, Timestamp> lastOpDate;
}
