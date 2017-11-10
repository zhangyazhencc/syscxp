package com.syscxp.header.tunnel.aliEdgeRouter;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AliEdgeRouterConfigVO.class)
public class AliEdgeRouterConfigVO_ {
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,String> uuid;
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,String> aliRegionId;
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,String> physicalLineUuid;
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,String> switchPortUuid;
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<AliEdgeRouterConfigVO,Timestamp> createDate;


}
