package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SwitchVO.class)
public class SwitchVO_ {
    public static volatile SingularAttribute<SwitchVO, String> uuid;
    public static volatile SingularAttribute<SwitchVO, String> endpointUuid;
    public static volatile SingularAttribute<SwitchVO, String> name;
    public static volatile SingularAttribute<SwitchVO, String> code;
    public static volatile SingularAttribute<SwitchVO, String> brand;
    public static volatile SingularAttribute<SwitchVO, String> model;
    public static volatile SingularAttribute<SwitchVO, String> subModel;
    public static volatile SingularAttribute<SwitchVO, String> upperType;
    public static volatile SingularAttribute<SwitchVO, String> owner;
    public static volatile SingularAttribute<SwitchVO, Integer> vlanBegin;
    public static volatile SingularAttribute<SwitchVO, Integer> vlanEnd;
    public static volatile SingularAttribute<SwitchVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchVO, Timestamp> lastOpDate;
}
