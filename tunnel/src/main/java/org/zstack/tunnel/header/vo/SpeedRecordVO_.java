package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SpeedRecordVO.class)
public class SpeedRecordVO_ {
    public static volatile SingularAttribute<SpeedRecordVO, String> uuid;
    public static volatile SingularAttribute<SpeedRecordVO, String> tunnelUuid;
    public static volatile SingularAttribute<SpeedRecordVO, String> protocol;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> srcDirection;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> dstDirection;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> avgSpeed;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> minSpeed;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> maxSpeed;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> completed;
    public static volatile SingularAttribute<SpeedRecordVO, Integer> deleted;
    public static volatile SingularAttribute<SpeedRecordVO, Timestamp> createDate;
    public static volatile SingularAttribute<SpeedRecordVO, Timestamp> lastOpDate;

}
