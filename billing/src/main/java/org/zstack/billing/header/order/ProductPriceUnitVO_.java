package org.zstack.billing.header.order;

import org.zstack.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ProductPriceUnitVO.class)
public class ProductPriceUnitVO_ {

    public static volatile SingularAttribute<ProductPriceUnitVO, String> uuid;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> productName;
    public static volatile SingularAttribute<ProductPriceUnitVO, ProductType> productType;
    public static volatile SingularAttribute<ProductPriceUnitVO, Category> category;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> config;
    public static volatile SingularAttribute<ProductPriceUnitVO, Integer> priceUnit;
    public static volatile SingularAttribute<ProductPriceUnitVO, String> comment;
    public static volatile SingularAttribute<ProductPriceUnitVO, Timestamp> createDate;
    public static volatile SingularAttribute<ProductPriceUnitVO, Timestamp> lastOpDate;
}
