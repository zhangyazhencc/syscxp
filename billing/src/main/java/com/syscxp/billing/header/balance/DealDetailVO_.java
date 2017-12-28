package com.syscxp.billing.header.balance;

import com.syscxp.billing.header.renew.RenewVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(DealDetailVO.class)
public class DealDetailVO_ {

    public static volatile SingularAttribute<DealDetailVO, String> uuid;
    public static volatile SingularAttribute<DealDetailVO, String> accountUuid;
    public static volatile SingularAttribute<DealDetailVO, String> opAccountUuid;
    public static volatile SingularAttribute<DealDetailVO, String> opUserUuid;
    public static volatile SingularAttribute<DealDetailVO, DealType> type;
    public static volatile SingularAttribute<DealDetailVO, BigDecimal> expend;
    public static volatile SingularAttribute<DealDetailVO, BigDecimal> income;
    public static volatile SingularAttribute<DealDetailVO, DealWay> dealWay;
    public static volatile SingularAttribute<DealDetailVO, DealState> state;
    public static volatile SingularAttribute<DealDetailVO, Timestamp> finishTime;
    public static volatile SingularAttribute<DealDetailVO, BigDecimal> balance;
    public static volatile SingularAttribute<DealDetailVO, Timestamp> createDate;
    public static volatile SingularAttribute<DealDetailVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<DealDetailVO, String> outTradeNO;
    public static volatile SingularAttribute<DealDetailVO, String> tradeNO;
    public static volatile SingularAttribute<DealDetailVO, String> orderUuid;
}
