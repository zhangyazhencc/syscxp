package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-17.
 * @Description: 告警模板.
 */
@StaticMetamodel(AlarmTemplateVO.class)
public class AlarmTemplateVO_ extends BaseVO_ {
    public static volatile SingularAttribute<AlarmEventVO, ProductType> productType;
    public static volatile SingularAttribute<AlarmEventVO, String> monitorTargetUuid;
    public static volatile SingularAttribute<AlarmEventVO, AlarmStatus> status;
}
