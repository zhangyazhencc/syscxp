package com.syscxp.header.billing;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ProductPriceUnitVO.class)
public class ProductPriceUnitVO_ {

    public static volatile SingularAttribute<ProductPriceUnitVO, String> uuid;
    public static volatile SingularAttribute<ProductPriceUnitVO, ProductType> productTypeCode;
    public static volatile SingularAttribute<ProductPriceUnitVO, Category> categoryCode;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> productTypeName;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> productCategoryUuid;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> categoryName;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> areaCode;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> areaName;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> lineCode;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> lineName;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> configCode;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> configName;
    public static volatile SingularAttribute<ProductPriceUnitVO, Integer> unitPrice;
    public static volatile SingularAttribute<ProductPriceUnitVO, Timestamp> createDate;
    public static volatile SingularAttribute<ProductPriceUnitVO, Timestamp> lastOpDate;
}
