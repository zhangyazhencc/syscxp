package org.zstack.billing.header.balance;

import org.zstack.billing.header.order.Category;
import org.zstack.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountDischargeVO.class)
public class AccountDischargeVO_ {

    public static volatile SingularAttribute<AccountDischargeVO, String> uuid;
    public static volatile SingularAttribute<AccountDischargeVO, String> accountUuid;
    public static volatile SingularAttribute<AccountDischargeVO, String> categoryName;
    public static volatile SingularAttribute<AccountDischargeVO, String> productTypeName;
    public static volatile SingularAttribute<AccountDischargeVO, Integer> disCharge;
    public static volatile SingularAttribute<AccountDischargeVO, ProductType> productType;
    public static volatile SingularAttribute<AccountDischargeVO, Category> category;
    public static volatile SingularAttribute<AccountDischargeVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountDischargeVO, Timestamp> lastOpDate;
}
