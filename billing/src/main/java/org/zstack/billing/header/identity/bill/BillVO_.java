package org.zstack.billing.header.identity.bill;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(BillVO.class)
public class BillVO_ {
    public static volatile SingularAttribute<BillVO, String> uuid;
    public static volatile SingularAttribute<BillVO, String> accountUuid;
    public static volatile SingularAttribute<BillVO, Timestamp> timeStart;
    public static volatile SingularAttribute<BillVO, Timestamp> timeEnd;
    public static volatile SingularAttribute<BillVO, BigDecimal> totolPayCash;
    public static volatile SingularAttribute<BillVO, BigDecimal> totalPayPresent;
    public static volatile SingularAttribute<BillVO, BigDecimal> totalIncomeCash;
    public static volatile SingularAttribute<BillVO, BigDecimal> totalIncomePresent;
    public static volatile SingularAttribute<BillVO, BigDecimal> repay;
    public static volatile SingularAttribute<BillVO, BigDecimal> cashBalance;
    public static volatile SingularAttribute<BillVO, Timestamp> billDate;
    public static volatile SingularAttribute<BillVO, Timestamp> createDate;
    public static volatile SingularAttribute<BillVO, Timestamp> lastOpDate;
}
