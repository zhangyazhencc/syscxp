package org.zstack.billing.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(OrderVO.class)
public class OrderVO_ {
    public static volatile SingularAttribute<OrderVO, String> uuid;
    public static volatile SingularAttribute<OrderVO, OrderType> orderType;
    public static volatile SingularAttribute<OrderVO, Timestamp> payTime;
    public static volatile SingularAttribute<OrderVO, OrderState> orderState;
    public static volatile SingularAttribute<OrderVO, BigDecimal> prderPayPresent;
    public static volatile SingularAttribute<OrderVO, BigDecimal> orderPayCash;
    public static volatile SingularAttribute<OrderVO, String> accountUuid;
    public static volatile SingularAttribute<OrderVO, Timestamp> productEffectTimeStart;
    public static volatile SingularAttribute<OrderVO, Timestamp> productEffectTimeEnd;
    public static volatile SingularAttribute<OrderVO, Timestamp> createDate;
    public static volatile SingularAttribute<OrderVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<OrderVO, String> productName;
    public static volatile SingularAttribute<OrderVO, ProductType> productType;
    public static volatile SingularAttribute<OrderVO, BigDecimal> productDiscount;
    public static volatile SingularAttribute<OrderVO, ProductChargeModel> productChargeModel;

}
