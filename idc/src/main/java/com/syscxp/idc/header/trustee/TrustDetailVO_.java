package com.syscxp.idc.header.trustee;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(TrustDetailVO.class)
public class TrustDetailVO_ {

    public static volatile SingularAttribute<TrustDetailVO,String> uuid;
    public static volatile SingularAttribute<TrustDetailVO,String> name;
    public static volatile SingularAttribute<TrustDetailVO,String> trusteeUuid;
    public static volatile SingularAttribute<TrustDetailVO,BigDecimal> cost;
    public static volatile SingularAttribute<TrustDetailVO,String> description;
    public static volatile SingularAttribute<TrustDetailVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<TrustDetailVO,Timestamp> createDate;

}
