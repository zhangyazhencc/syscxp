package com.syscxp.idc.header.trustee;


import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(TrusteeAO.class)
public class TrusteeAO_ {

    public static volatile SingularAttribute<TrusteeAO,String> uuid;
    public static volatile SingularAttribute<TrusteeAO,String> name;
    public static volatile SingularAttribute<TrusteeAO,String> description;
    public static volatile SingularAttribute<TrusteeAO,String> accountName;
    public static volatile SingularAttribute<TrusteeAO,String> accountUuid;
    public static volatile SingularAttribute<TrusteeAO,String> company;
    public static volatile SingularAttribute<TrusteeAO,String> contractNum;
    public static volatile SingularAttribute<TrusteeAO,String> nodeUuid;
    public static volatile SingularAttribute<TrusteeAO,String> nodeName;
    public static volatile SingularAttribute<TrusteeAO,ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<TrusteeAO,Integer> duration;
    public static volatile SingularAttribute<TrusteeAO,BigDecimal> totalCost;
    public static volatile SingularAttribute<TrusteeAO,Timestamp> expireDate;
    public static volatile SingularAttribute<TrusteeAO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<TrusteeAO,Timestamp> createDate;

}
