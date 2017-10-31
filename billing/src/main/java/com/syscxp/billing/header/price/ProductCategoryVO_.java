package com.syscxp.billing.header.price;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(ProductCategoryVO.class)
public class ProductCategoryVO_ {

    public static volatile SingularAttribute<ProductCategoryVO, String> uuid;
    public static volatile SingularAttribute<ProductCategoryVO, String> code;
    public static volatile SingularAttribute<ProductCategoryVO, String> name;
    public static volatile SingularAttribute<ProductCategoryVO, String> productTypeCode;
    public static volatile SingularAttribute<ProductCategoryVO, String> productTypeName;
    public static volatile SingularAttribute<ProductCategoryVO, Timestamp> createDate;
    public static volatile SingularAttribute<ProductCategoryVO, Timestamp> lastOpDate;
}
