package com.syscxp.header.tunnel.network;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3NetworkAO.class)
public class L3NetworkAO_ {

    public static volatile SingularAttribute<L3NetworkAO,String> uuid;
    public static volatile SingularAttribute<L3NetworkAO,String> accountUuid;
    public static volatile SingularAttribute<L3NetworkAO,String> ownerAccountUuid;
    public static volatile SingularAttribute<L3NetworkAO,String> name;
    public static volatile SingularAttribute<L3NetworkAO,String> code;
    public static volatile SingularAttribute<L3NetworkAO,Integer> vid;
    public static volatile SingularAttribute<L3NetworkAO,String> type;
    public static volatile SingularAttribute<L3NetworkAO,Integer> endPointNum;
    public static volatile SingularAttribute<L3NetworkAO,String> description;
    public static volatile SingularAttribute<L3NetworkAO,Integer> duration;
    public static volatile SingularAttribute<L3NetworkAO,ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<L3NetworkAO,Integer> maxModifies;
    public static volatile SingularAttribute<L3NetworkAO,Timestamp> expireDate;
    public static volatile SingularAttribute<L3NetworkAO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3NetworkAO,Timestamp> createDate;



}
