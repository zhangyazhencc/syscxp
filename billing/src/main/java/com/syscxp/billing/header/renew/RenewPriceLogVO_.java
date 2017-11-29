package com.syscxp.billing.header.renew;


import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(RenewPriceLogVO.class)
public class RenewPriceLogVO_ {

    public static volatile SingularAttribute<RenewVO, String> uuid;
    public static volatile SingularAttribute<RenewVO, String> accountUuid;
    public static volatile SingularAttribute<RenewVO, String> productUuid;
    public static volatile SingularAttribute<RenewVO, String> opAccountUuid;
    public static volatile SingularAttribute<RenewVO, String> opUserUuid;
    public static volatile SingularAttribute<RenewVO, BigDecimal> originPrice;
    public static volatile SingularAttribute<RenewVO, BigDecimal> nowPrice;
    public static volatile SingularAttribute<RenewVO, Timestamp> createDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> lastOpDate;
}
