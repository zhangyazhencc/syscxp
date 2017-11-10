package com.syscxp.header.tunnel.aliEdgeRouter;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AliUserVO.class)
public class AliUserVO_ {
    public static volatile SingularAttribute<AliUserVO,String> uuid;
    public static volatile SingularAttribute<AliUserVO,String> accountUuid;
    public static volatile SingularAttribute<AliUserVO,String> aliAccountUuid;
    public static volatile SingularAttribute<AliUserVO,String> aliAccessKeyID;
    public static volatile SingularAttribute<AliUserVO,String> aliAccessKeySecret;
    public static volatile SingularAttribute<AliUserVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<AliUserVO,Timestamp> createDate;

}
