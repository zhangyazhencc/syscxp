package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-10-22
 */
@StaticMetamodel(ProductsLogVO.class)
public class ProductsLogVO_ {

    public static volatile SingularAttribute<ProductsLogVO, String> uuid;
    public static volatile SingularAttribute<ProductsLogVO, String> productUuid;
    public static volatile SingularAttribute<ProductsLogVO, String> accountUuid;
    public static volatile SingularAttribute<ProductsLogVO, String> opAccountUuid;
    public static volatile SingularAttribute<ProductsLogVO, OrderType> type;
    public static volatile SingularAttribute<ProductsLogVO, ProductsStatue> statue;
    public static volatile SingularAttribute<ProductsLogVO, Integer> duration;
    public static volatile SingularAttribute<ProductsLogVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<ProductsLogVO, Timestamp> expireDate;
    public static volatile SingularAttribute<ProductsLogVO, Timestamp> createDate;
}
