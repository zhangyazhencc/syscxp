package com.syscxp.header.tunnel.monitor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@StaticMetamodel(SpeedRecordsVO.class)
public class SpeedRecordsVO_ {
    public static volatile SingularAttribute<SpeedRecordsVO, String> uuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> accountUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> tunnelUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> srcTunnelMonitorUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> dstTunnelMonitorUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> srcNodeUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, String> dstNodeUuid;
    public static volatile SingularAttribute<SpeedRecordsVO, ProtocolType> protocolType;

    public static volatile SingularAttribute<SpeedRecordsVO, Integer> duration;
    public static volatile SingularAttribute<SpeedRecordsVO, Integer> avgSpeed;
    public static volatile SingularAttribute<SpeedRecordsVO, Integer> maxSpeed;
    public static volatile SingularAttribute<SpeedRecordsVO, Integer> minSpeed;
    public static volatile SingularAttribute<SpeedRecordsVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<SpeedRecordsVO, Timestamp> createDate;

    public static volatile SingularAttribute<SpeedRecordsVO, SpeedRecordStatus> status;
}
