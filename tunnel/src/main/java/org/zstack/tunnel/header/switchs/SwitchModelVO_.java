package org.zstack.tunnel.header.switchs;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-06
 */
@StaticMetamodel(SwitchModelVO.class)
public class SwitchModelVO_ {
    public static volatile SingularAttribute<SwitchModelVO, String> uuid;
    public static volatile SingularAttribute<SwitchModelVO, String> brand;
    public static volatile SingularAttribute<SwitchModelVO, String> model;
    public static volatile SingularAttribute<SwitchModelVO, String> subModel;
    public static volatile SingularAttribute<SwitchModelVO, Timestamp> createDate;
    public static volatile SingularAttribute<SwitchModelVO, Timestamp> lastOpDate;
}
