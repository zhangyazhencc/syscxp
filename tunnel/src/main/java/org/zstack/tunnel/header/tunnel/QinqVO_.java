package org.zstack.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-11
 */
@StaticMetamodel(QinqVO.class)
public class QinqVO_ {

    public static volatile SingularAttribute<QinqVO, String> uuid;
    public static volatile SingularAttribute<QinqVO, String> tunnelInterfaceUuid;
    public static volatile SingularAttribute<QinqVO, Integer> startVlan;
    public static volatile SingularAttribute<QinqVO, Integer> endVlan;
    public static volatile SingularAttribute<QinqVO, Timestamp> createDate;
    public static volatile SingularAttribute<QinqVO, Timestamp> lastOpDate;
}
