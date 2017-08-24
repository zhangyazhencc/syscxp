package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-24
 */
@StaticMetamodel(SwitchAO.class)
public class SwitchAO_ {
    public static volatile SingularAttribute<SwitchVO, String> uuid;
    public static volatile SingularAttribute<SwitchVO, String> endpointUuid;
    public static volatile SingularAttribute<SwitchVO, String> code;
    public static volatile SingularAttribute<SwitchVO, String> name;
    public static volatile SingularAttribute<SwitchVO, String> brand;
    public static volatile SingularAttribute<SwitchVO, String> switchModelUuid;
    public static volatile SingularAttribute<SwitchVO, SwitchUpperType> upperType;
    public static volatile SingularAttribute<SwitchVO, Integer> enabled;
    public static volatile SingularAttribute<SwitchVO, String> owner;
    public static volatile SingularAttribute<SwitchVO, String> rack;
    public static volatile SingularAttribute<SwitchVO, String> description;
    public static volatile SingularAttribute<SwitchVO, String> mIP;
    public static volatile SingularAttribute<SwitchVO, String> username;
    public static volatile SingularAttribute<SwitchVO, String> password;
    public static volatile SingularAttribute<SwitchVO, SwitchStatus> status;
    public static volatile SingularAttribute<SwitchVO, Integer> isPrivate;
    public static volatile SingularAttribute<SwitchVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<SwitchVO, Timestamp> createDate;
}
