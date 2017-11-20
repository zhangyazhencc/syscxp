package com.syscxp.header.tunnel.aliEdgeRouter;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AliEdgeRouterAO.class)
public class AliEdgeRouterAO_ {
    public static volatile SingularAttribute<AliEdgeRouterAO, String> uuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> accountUuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> tunnelUuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> aliAccountUuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> aliRegionId;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> name;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> description;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> vbrUuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> physicalLineUuid;
    public static volatile SingularAttribute<AliEdgeRouterAO, String> vlan;
    public static volatile SingularAttribute<AliEdgeRouterAO, Timestamp> createDate;
    public static volatile SingularAttribute<AliEdgeRouterAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<AliEdgeRouterAO, Boolean> isCreateFlag;
}
