package com.syscxp.tunnel.header.aliEdgeRouter;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AliUserVO.class)
public class AliUserVO_ {
    public static volatile SingularAttribute<AliUserVO,String> uuid;
    public static volatile SingularAttribute<AliUserVO,String> accountUuid;
    public static volatile SingularAttribute<AliUserVO,String> aliAccountUuid;
    public static volatile SingularAttribute<AliUserVO,String> AliAccessKeyID;
    public static volatile SingularAttribute<AliUserVO,String> AliAccessKeySecret;
    public static volatile SingularAttribute<AliUserVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<AliUserVO,Timestamp> createDate;

}
