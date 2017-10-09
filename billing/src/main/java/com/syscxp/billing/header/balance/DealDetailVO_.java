package com.syscxp.billing.header.balance;

import com.syscxp.billing.header.renew.RenewVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(DealDetailVO.class)
public class DealDetailVO_ {

    public static volatile SingularAttribute<RenewVO, String> uuid;
    public static volatile SingularAttribute<RenewVO, String> accountUuid;
    public static volatile SingularAttribute<RenewVO, String> opAccountUuid;
    public static volatile SingularAttribute<RenewVO, DealType> type;
    public static volatile SingularAttribute<RenewVO, BigDecimal> expend;
    public static volatile SingularAttribute<RenewVO, BigDecimal> income;
    public static volatile SingularAttribute<RenewVO, DealWay> dealWay;
    public static volatile SingularAttribute<RenewVO, DealState> state;
    public static volatile SingularAttribute<RenewVO, Timestamp> finishTime;
    public static volatile SingularAttribute<RenewVO, BigDecimal> balance;
    public static volatile SingularAttribute<RenewVO, Timestamp> createDate;
    public static volatile SingularAttribute<RenewVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<RenewVO, String> outTradeNO;
    public static volatile SingularAttribute<RenewVO, String> tradeNO;
}
