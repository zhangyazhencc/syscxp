package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-06
 */
@StaticMetamodel(SwitchAttributionAO.class)
public class SwitchAttributionAO_ {
    public static volatile SingularAttribute<SwitchAttributionAO, String> uuid;
    public static volatile SingularAttribute<SwitchAttributionAO, String> switchModelUuid;
    public static volatile SingularAttribute<SwitchAttributionAO, String> code;
    public static volatile SingularAttribute<SwitchAttributionAO, String> name;
    public static volatile SingularAttribute<SwitchAttributionAO, String> brand;
    public static volatile SingularAttribute<SwitchAttributionAO, String> owner;
    public static volatile SingularAttribute<SwitchAttributionAO, String> rack;
    public static volatile SingularAttribute<SwitchAttributionAO, String> description;
    public static volatile SingularAttribute<SwitchAttributionAO, String> mIP;
    public static volatile SingularAttribute<SwitchAttributionAO, String> username;
    public static volatile SingularAttribute<SwitchAttributionAO, String> password;
    public static volatile SingularAttribute<SwitchAttributionAO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchAttributionAO, Timestamp> lastOpDate;
}
