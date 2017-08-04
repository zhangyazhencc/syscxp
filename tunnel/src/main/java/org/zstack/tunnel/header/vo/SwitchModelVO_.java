package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SwitchModelVO.class)
public class SwitchModelVO_ {
    public static volatile SingularAttribute<SwitchModelVO, String> uuid;
    public static volatile SingularAttribute<SwitchModelVO, String> model;
    public static volatile SingularAttribute<SwitchModelVO, String> subModel;
    public static volatile SingularAttribute<SwitchModelVO, String> mpls;
    public static volatile SingularAttribute<SwitchModelVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchModelVO, Timestamp> lastOpDate;

}
