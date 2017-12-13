package com.syscxp.billing.header.renew;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(RenewVO.class)
public class RenewVO_ {

    public static volatile SingularAttribute<RenewVO, String> uuid;
    public static volatile SingularAttribute<RenewVO, String> accountUuid;
    public static volatile SingularAttribute<RenewVO, Boolean> isRenewAuto;
    public static volatile SingularAttribute<RenewVO, String> productUuid;
    public static volatile SingularAttribute<RenewVO, String> productName;
    public static volatile SingularAttribute<RenewVO, String> descriptionData;
    public static volatile SingularAttribute<RenewVO, BigDecimal> priceOneMonth;
    public static volatile SingularAttribute<RenewVO, BigDecimal> priceDiscount;
    public static volatile SingularAttribute<RenewVO, ProductType> productType;
    public static volatile SingularAttribute<RenewVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<RenewVO, Timestamp> createDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> expiredTime;

}
