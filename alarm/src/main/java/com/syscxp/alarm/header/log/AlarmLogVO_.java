package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@StaticMetamodel(AlarmLogVO.class)
public class AlarmLogVO_  extends BaseVO_ {
    public static volatile SingularAttribute<AlarmLogVO, String> productUuid;
    public static volatile SingularAttribute<AlarmLogVO, String> productName;
    public static volatile SingularAttribute<AlarmLogVO, ProductType> productType;
    public static volatile SingularAttribute<AlarmLogVO, Timestamp> alarmTime;
    public static volatile SingularAttribute<AlarmLogVO, Integer> duration;
    public static volatile SingularAttribute<AlarmLogVO, TimeUnit> durationTimeUnit;
    public static volatile SingularAttribute<AlarmLogVO, String> alarmContent;
    public static volatile SingularAttribute<AlarmLogVO, Timestamp> resumeTime;
    public static volatile SingularAttribute<AlarmLogVO, AlarmStatus> status;
}