package com.syscxp.idc.header;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(IdcDetailVO.class)
public class IdcDetailVO_ {

    public static volatile SingularAttribute<IdcDetailVO,String> uuid;
    public static volatile SingularAttribute<IdcDetailVO,String> name;
    public static volatile SingularAttribute<IdcDetailVO,String> trusteeUuid;
    public static volatile SingularAttribute<IdcDetailVO,BigDecimal> cost;
    public static volatile SingularAttribute<IdcDetailVO,String> description;
    public static volatile SingularAttribute<IdcDetailVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<IdcDetailVO,Timestamp> createDate;

}
