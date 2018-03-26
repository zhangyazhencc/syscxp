package com.syscxp.header.tunnel.network;

import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3NetworkVO.class)
public class L3NetworkVO_ {

    public static volatile SingularAttribute<L3NetworkVO,String> uuid;
    public static volatile SingularAttribute<L3NetworkVO, Long> number;
    public static volatile SingularAttribute<L3NetworkVO,String> accountUuid;
    public static volatile SingularAttribute<L3NetworkVO,String> ownerAccountUuid;
    public static volatile SingularAttribute<L3NetworkVO,String> name;
    public static volatile SingularAttribute<L3NetworkVO,String> code;
    public static volatile SingularAttribute<L3NetworkVO,Integer> vid;
    public static volatile SingularAttribute<L3NetworkVO,String> type;
    public static volatile SingularAttribute<L3NetworkVO,Integer> endpointNum;
    public static volatile SingularAttribute<L3NetworkVO,String> description;
    public static volatile SingularAttribute<L3NetworkVO,Integer> duration;
    public static volatile SingularAttribute<L3NetworkVO,ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<L3NetworkVO,Integer> maxModifies;
    public static volatile SingularAttribute<L3NetworkVO,Timestamp> expireDate;
    public static volatile SingularAttribute<L3NetworkVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3NetworkVO,Timestamp> createDate;
}
