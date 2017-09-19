package org.zstack.billing.header.renew;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
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
