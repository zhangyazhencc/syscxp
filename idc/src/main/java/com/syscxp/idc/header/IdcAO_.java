package com.syscxp.idc.header;


import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(IdcAO.class)
public class IdcAO_ {

    public static volatile SingularAttribute<IdcAO,String> uuid;
    public static volatile SingularAttribute<IdcAO,String> name;
    public static volatile SingularAttribute<IdcAO,String> description;
    public static volatile SingularAttribute<IdcAO,String> accountName;
    public static volatile SingularAttribute<IdcAO,String> accountUuid;
    public static volatile SingularAttribute<IdcAO,String> company;
    public static volatile SingularAttribute<IdcAO,String> contractNum;
    public static volatile SingularAttribute<IdcAO,String> nodeUuid;
    public static volatile SingularAttribute<IdcAO,String> nodeName;
    public static volatile SingularAttribute<IdcAO,ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<IdcAO,Integer> duration;
    public static volatile SingularAttribute<IdcAO,BigDecimal> totalCost;
    public static volatile SingularAttribute<IdcAO,Timestamp> expireDate;
    public static volatile SingularAttribute<IdcAO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<IdcAO,Timestamp> createDate;

}
