package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-24
 */
@StaticMetamodel(SwitchAO.class)
public class SwitchAO_ {
    public static volatile SingularAttribute<SwitchAO, String> uuid;
    public static volatile SingularAttribute<SwitchAO, String> endpointUuid;
    public static volatile SingularAttribute<SwitchAO, String> code;
    public static volatile SingularAttribute<SwitchAO, String> name;
    public static volatile SingularAttribute<SwitchAO, String> brand;
    public static volatile SingularAttribute<SwitchAO, String> switchModelUuid;
    public static volatile SingularAttribute<SwitchAO, SwitchUpperType> upperType;
    public static volatile SingularAttribute<SwitchAO, Integer> enabled;
    public static volatile SingularAttribute<SwitchAO, String> owner;
    public static volatile SingularAttribute<SwitchAO, String> rack;
    public static volatile SingularAttribute<SwitchAO, String> description;
    public static volatile SingularAttribute<SwitchAO, String> mIP;
    public static volatile SingularAttribute<SwitchAO, String> username;
    public static volatile SingularAttribute<SwitchAO, String> password;
    public static volatile SingularAttribute<SwitchAO, SwitchStatus> status;
    public static volatile SingularAttribute<SwitchAO, Integer> isPrivate;
    public static volatile SingularAttribute<SwitchAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<SwitchAO, Timestamp> createDate;
}
