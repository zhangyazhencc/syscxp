package org.zstack.tunnel.header.tunnel;

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
    public static volatile SingularAttribute<TunnelAO, String> networkUuid;
    public static volatile SingularAttribute<TunnelAO, String> name;
    public static volatile SingularAttribute<TunnelAO, Long> bandwidth;
    public static volatile SingularAttribute<TunnelAO, Double> distance;
    public static volatile SingularAttribute<TunnelAO, TunnelState> state;
    public static volatile SingularAttribute<TunnelAO, TunnelStatus> status;
    public static volatile SingularAttribute<TunnelAO, TunnelMonitorState> monitorState;
    public static volatile SingularAttribute<TunnelAO, Integer> months;
    public static volatile SingularAttribute<TunnelAO, String> description;
    public static volatile SingularAttribute<TunnelAO, Timestamp> expiredDate;
    public static volatile SingularAttribute<TunnelAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<TunnelAO, Timestamp> createDate;
}
