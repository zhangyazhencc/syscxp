package com.syscxp.header.billing;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ProductCategoryVO.class)
public class ProductCategoryVO_ {

    public static volatile SingularAttribute<ProductCategoryVO, String> uuid;
    public static volatile SingularAttribute<ProductCategoryVO, ProductCategory> code;
    public static volatile SingularAttribute<ProductCategoryVO, String> name;
    public static volatile SingularAttribute<ProductCategoryVO, ProductType> productTypeCode;
    public static volatile SingularAttribute<ProductCategoryVO, String> productTypeName;
    public static volatile SingularAttribute<ProductCategoryVO, Timestamp> createDate;
    public static volatile SingularAttribute<ProductCategoryVO, Timestamp> lastOpDate;
}
