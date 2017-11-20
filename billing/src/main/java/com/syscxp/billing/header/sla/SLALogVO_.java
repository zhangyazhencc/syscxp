package com.syscxp.billing.header.sla;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(SLALogVO.class)
public class SLALogVO_ {

    public static volatile SingularAttribute<SLACompensateVO, String> uuid;
    public static volatile SingularAttribute<SLACompensateVO, String> accountUuid;
    public static volatile SingularAttribute<SLACompensateVO, String> productUuid;
    public static volatile SingularAttribute<SLACompensateVO, Integer> duration;
    public static volatile SingularAttribute<SLACompensateVO, BigDecimal> slaPrice;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> timeStart;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> timeEnd;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> createDate;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> lastOpDate;
}
