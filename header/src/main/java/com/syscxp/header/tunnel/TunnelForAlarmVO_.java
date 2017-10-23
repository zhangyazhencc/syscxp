package com.syscxp.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/23
 */
@StaticMetamodel(TunnelForAlarmVO.class)
public class TunnelForAlarmVO_ {

    public static volatile SingularAttribute<TunnelForAlarmVO, String> uuid;
    public static volatile SingularAttribute<TunnelForAlarmVO, String> accountUuid;
    public static volatile SingularAttribute<TunnelForAlarmVO, Integer> vsi;
    public static volatile SingularAttribute<TunnelForAlarmVO, String> monitorCidr;
    public static volatile SingularAttribute<TunnelForAlarmVO, String> name;
    public static volatile SingularAttribute<TunnelForAlarmVO, Long> bandwidth;
    public static volatile SingularAttribute<TunnelForAlarmVO, Double> distance;
    public static volatile SingularAttribute<TunnelForAlarmVO, TunnelState> state;
    public static volatile SingularAttribute<TunnelForAlarmVO, TunnelStatus> status;
    public static volatile SingularAttribute<TunnelForAlarmVO, TunnelMonitorState> monitorState;
    public static volatile SingularAttribute<TunnelForAlarmVO, Integer> duration;
    public static volatile SingularAttribute<TunnelForAlarmVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<TunnelForAlarmVO, Integer> maxModifies;
    public static volatile SingularAttribute<TunnelForAlarmVO, String> description;
    public static volatile SingularAttribute<TunnelForAlarmVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<TunnelForAlarmVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<TunnelForAlarmVO, Timestamp> createDate;
}
