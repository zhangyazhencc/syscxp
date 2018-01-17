package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-15.
 * @Description: .
 */

@StaticMetamodel(AlarmEventVO.class)
public class AlarmEventVO_  extends BaseVO_ {
    public static volatile SingularAttribute<AlarmEventVO, String> id;
    public static volatile SingularAttribute<AlarmEventVO, String> expressionUuid;
    public static volatile SingularAttribute<AlarmEventVO, String> endpoint;
    public static volatile SingularAttribute<AlarmEventVO, AlarmStatus> status;
    public static volatile SingularAttribute<AlarmEventVO, String> leftValue;
    public static volatile SingularAttribute<AlarmEventVO, String> currentStep;
    public static volatile SingularAttribute<AlarmEventVO, String> regulationId;
    public static volatile SingularAttribute<AlarmEventVO, String> resourceUuid;
    public static volatile SingularAttribute<AlarmEventVO, Timestamp> eventTime;
}

