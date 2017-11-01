package com.syscxp.header.billing;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountDiscountVO.class)
public class AccountDiscountVO_ {

    public static volatile SingularAttribute<AccountDiscountVO, String> uuid;
    public static volatile SingularAttribute<AccountDiscountVO, String> accountUuid;
    public static volatile SingularAttribute<AccountDiscountVO, String> productCategoryUuid;
    public static volatile SingularAttribute<AccountDiscountVO, Integer> discount;
    public static volatile SingularAttribute<AccountDiscountVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountDiscountVO, Timestamp> lastOpDate;
}
