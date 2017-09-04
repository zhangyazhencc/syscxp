package org.zstack.billing.header.order;

import org.zstack.billing.header.balance.ProductChargeModel;
import org.zstack.billing.header.balance.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(OrderVO.class)
public class OrderVO_ {
    public static volatile SingularAttribute<OrderVO, String> uuid;
    public static volatile SingularAttribute<OrderVO, OrderType> type;
    public static volatile SingularAttribute<OrderVO, Timestamp> payTime;
    public static volatile SingularAttribute<OrderVO, OrderState> state;
    public static volatile SingularAttribute<OrderVO, BigDecimal> payPresent;
    public static volatile SingularAttribute<OrderVO, BigDecimal> payCash;
    public static volatile SingularAttribute<OrderVO, String> accountUuid;
    public static volatile SingularAttribute<OrderVO, Timestamp> productEffectTimeStart;
    public static volatile SingularAttribute<OrderVO, Timestamp> productEffectTimeEnd;
    public static volatile SingularAttribute<OrderVO, Timestamp> createDate;
    public static volatile SingularAttribute<OrderVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<OrderVO, String> productName;
    public static volatile SingularAttribute<OrderVO, ProductType> productType;
    public static volatile SingularAttribute<OrderVO, BigDecimal> productDiscount;
    public static volatile SingularAttribute<OrderVO, BigDecimal> price;
    public static volatile SingularAttribute<OrderVO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<OrderVO, BigDecimal> originalPrice;
    public static volatile SingularAttribute<OrderVO, String> productUuid;
    public static volatile SingularAttribute<OrderVO, Integer> duration;
    public static volatile SingularAttribute<OrderVO, String> productUnitPriceUuid;

}
