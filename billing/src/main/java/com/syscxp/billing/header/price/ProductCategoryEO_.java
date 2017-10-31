package com.syscxp.billing.header.price;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(ProductCategoryEO.class)
public class ProductCategoryEO_ {

    public static volatile SingularAttribute<ProductCategoryEO, String> uuid;
    public static volatile SingularAttribute<ProductCategoryEO, String> code;
    public static volatile SingularAttribute<ProductCategoryEO, String> name;
    public static volatile SingularAttribute<ProductCategoryEO, String> productTypeCode;
    public static volatile SingularAttribute<ProductCategoryEO, String> productTypeName;
    public static volatile SingularAttribute<ProductCategoryEO, Timestamp> createDate;
    public static volatile SingularAttribute<ProductCategoryEO, Timestamp> lastOpDate;
}
