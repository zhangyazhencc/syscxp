package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountDiscountVO.class)
public class AccountDiscountVO_ {

    public static volatile SingularAttribute<AccountDiscountVO, String> uuid;
    public static volatile SingularAttribute<AccountDiscountVO, String> accountUuid;
    public static volatile SingularAttribute<AccountDiscountVO, String> categoryName;
    public static volatile SingularAttribute<AccountDiscountVO, String> productTypeName;
    public static volatile SingularAttribute<AccountDiscountVO, Integer> discount;
    public static volatile SingularAttribute<AccountDiscountVO, ProductType> productType;
    public static volatile SingularAttribute<AccountDiscountVO, Category> category;
    public static volatile SingularAttribute<AccountDiscountVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountDiscountVO, Timestamp> lastOpDate;
}
