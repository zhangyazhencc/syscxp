package org.zstack.billing.header.renew;

import org.zstack.billing.header.balance.ProductChargeModel;
import org.zstack.billing.header.balance.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(PriceRefRenewVO.class)
public class PriceRefRenewVO_ {

    public static volatile SingularAttribute<RenewVO, String> uuid;
    public static volatile SingularAttribute<RenewVO, String> accountUuid;
    public static volatile SingularAttribute<RenewVO, String> renewUuid;
    public static volatile SingularAttribute<RenewVO, String> productPriceUnitUuid;
    public static volatile SingularAttribute<RenewVO, Timestamp> createDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> lastOpDate;

}
