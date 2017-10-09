package com.syscxp.tunnel.header.switchs;

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
    public static volatile SingularAttribute<SwitchAO, String> physicalSwitchUuid;
    public static volatile SingularAttribute<SwitchAO, String> description;
    public static volatile SingularAttribute<SwitchAO, SwitchState> state;
    public static volatile SingularAttribute<SwitchAO, SwitchStatus> status;
    public static volatile SingularAttribute<SwitchAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<SwitchAO, Timestamp> createDate;
}
