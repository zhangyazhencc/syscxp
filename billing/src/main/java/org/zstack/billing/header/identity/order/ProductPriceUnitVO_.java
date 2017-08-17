package org.zstack.billing.header.identity.order;

import org.zstack.billing.header.identity.balance.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
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
