package org.zstack.billing.header.identity.renew;

import org.zstack.billing.header.identity.balance.ProductChargeModel;
import org.zstack.billing.header.identity.balance.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(RenewVO.class)
public class RenewVO_ {

    public static volatile SingularAttribute<RenewVO, String> uuid;
    public static volatile SingularAttribute<RenewVO, String> accountUuid;
    public static volatile SingularAttribute<RenewVO, Boolean> isRenewAuto;
    public static volatile SingularAttribute<RenewVO, String> productUuid;
    public static volatile SingularAttribute<RenewVO, String> productName;
    public static volatile SingularAttribute<RenewVO, ProductType> productType;
    public static volatile SingularAttribute<RenewVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<RenewVO, Integer> duration;
    public static volatile SingularAttribute<RenewVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> createDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<RenewVO, String> productUnitPriceUuid;

}
