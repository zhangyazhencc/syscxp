package com.syscxp.header.tunnel.edgeLine;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/1/9
 */
@StaticMetamodel(EdgeLineAO.class)
public class EdgeLineAO_ {
    public static volatile SingularAttribute<EdgeLineAO, String> uuid;
    public static volatile SingularAttribute<EdgeLineAO, Long> number;
    public static volatile SingularAttribute<EdgeLineAO, String> accountUuid;
    public static volatile SingularAttribute<EdgeLineAO, String> interfaceUuid;
    public static volatile SingularAttribute<EdgeLineAO, String> endpointUuid;
    public static volatile SingularAttribute<EdgeLineAO, String> type;
    public static volatile SingularAttribute<EdgeLineAO, String> destinationInfo;
    public static volatile SingularAttribute<EdgeLineAO, String> description;
    public static volatile SingularAttribute<EdgeLineAO, EdgeLineState> state;
    public static volatile SingularAttribute<EdgeLineAO, Integer> costPrices;
    public static volatile SingularAttribute<EdgeLineAO, Integer> prices;
    public static volatile SingularAttribute<EdgeLineAO, String> implementType;
    public static volatile SingularAttribute<EdgeLineAO, Timestamp> expireDate;
    public static volatile SingularAttribute<EdgeLineAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<EdgeLineAO, Timestamp> createDate;
}
