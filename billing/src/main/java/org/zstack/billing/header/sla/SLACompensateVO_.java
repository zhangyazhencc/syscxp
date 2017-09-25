package org.zstack.billing.header.sla;

import org.zstack.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SLACompensateVO.class)
public class SLACompensateVO_ {
    public static volatile SingularAttribute<SLACompensateVO, String> uuid;
    public static volatile SingularAttribute<SLACompensateVO, String> accountUuid;
    public static volatile SingularAttribute<SLACompensateVO, String> productUuid;
    public static volatile SingularAttribute<SLACompensateVO, ProductType> productType;
    public static volatile SingularAttribute<SLACompensateVO, String> productName;
    public static volatile SingularAttribute<SLACompensateVO, String> reason;
    public static volatile SingularAttribute<SLACompensateVO, String> comment;
    public static volatile SingularAttribute<SLACompensateVO, Integer> duration;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> timeStart;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> timeEnd;
    public static volatile SingularAttribute<SLACompensateVO, SLAState> state;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> createDate;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<SLACompensateVO, Timestamp> applyTime;
}
