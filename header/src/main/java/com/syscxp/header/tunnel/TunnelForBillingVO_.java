package com.syscxp.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/23
 */
@StaticMetamodel(TunnelForBillingVO.class)
public class TunnelForBillingVO_ {

    public static volatile SingularAttribute<TunnelForBillingVO, String> uuid;
    public static volatile SingularAttribute<TunnelForBillingVO, String> accountUuid;
    public static volatile SingularAttribute<TunnelForBillingVO, Integer> vsi;
    public static volatile SingularAttribute<TunnelForBillingVO, String> monitorCidr;
    public static volatile SingularAttribute<TunnelForBillingVO, String> name;
    public static volatile SingularAttribute<TunnelForBillingVO, Long> bandwidth;
    public static volatile SingularAttribute<TunnelForBillingVO, Double> distance;
    public static volatile SingularAttribute<TunnelForBillingVO, TunnelState> state;
    public static volatile SingularAttribute<TunnelForBillingVO, TunnelStatus> status;
    public static volatile SingularAttribute<TunnelForBillingVO, TunnelMonitorState> monitorState;
    public static volatile SingularAttribute<TunnelForBillingVO, Integer> duration;
    public static volatile SingularAttribute<TunnelForBillingVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<TunnelForBillingVO, Integer> maxModifies;
    public static volatile SingularAttribute<TunnelForBillingVO, String> description;
    public static volatile SingularAttribute<TunnelForBillingVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<TunnelForBillingVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<TunnelForBillingVO, Timestamp> createDate;
}
