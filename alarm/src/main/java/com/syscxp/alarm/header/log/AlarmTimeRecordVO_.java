package com.syscxp.alarm.header.log;

import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@StaticMetamodel(AlarmTimeRecordVO.class)
public class AlarmTimeRecordVO_ {
    public static volatile SingularAttribute<AlarmLogVO, String> tunnelUuid;
    public static volatile SingularAttribute<AlarmLogVO, String> eventId;
    public static volatile SingularAttribute<AlarmLogVO, String> status;
    public static volatile SingularAttribute<AlarmLogVO, ProductType> productType;
}
