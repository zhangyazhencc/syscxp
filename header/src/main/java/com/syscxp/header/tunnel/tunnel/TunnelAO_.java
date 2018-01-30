package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-11
 */
@StaticMetamodel(TunnelAO.class)
public class TunnelAO_ {

    public static volatile SingularAttribute<TunnelAO, String> uuid;
    public static volatile SingularAttribute<TunnelAO, String> accountUuid;
    public static volatile SingularAttribute<TunnelAO, String> ownerAccountUuid;
    public static volatile SingularAttribute<TunnelAO, Integer> vsi;
    public static volatile SingularAttribute<TunnelAO, String> monitorCidr;
    public static volatile SingularAttribute<TunnelAO, String> name;
    public static volatile SingularAttribute<TunnelAO, String> bandwidthOffering;
    public static volatile SingularAttribute<TunnelAO, Long> bandwidth;
    public static volatile SingularAttribute<TunnelAO, Double> distance;
    public static volatile SingularAttribute<TunnelAO, TunnelState> state;
    public static volatile SingularAttribute<TunnelAO, TunnelStatus> status;
    public static volatile SingularAttribute<TunnelAO, TunnelType> type;
    public static volatile SingularAttribute<TunnelAO, String> innerEndpointUuid;
    public static volatile SingularAttribute<TunnelAO, TunnelMonitorState> monitorState;
    public static volatile SingularAttribute<TunnelAO, Integer> duration;
    public static volatile SingularAttribute<TunnelAO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<TunnelAO, Integer> maxModifies;
    public static volatile SingularAttribute<TunnelAO, String> description;
    public static volatile SingularAttribute<TunnelAO, Timestamp> expireDate;
    public static volatile SingularAttribute<TunnelAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<TunnelAO, Timestamp> createDate;
}
