package org.zstack.billing.header.identity.receipt;



import org.zstack.billing.header.identity.ReceiptState;
import org.zstack.billing.header.identity.ReceiptType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(ReceiptVO.class)
public class ReceiptVO_ {

    public static volatile SingularAttribute<ReceiptVO, String> uuid;
    public static volatile SingularAttribute<ReceiptVO, String> accountUuid;
    public static volatile SingularAttribute<ReceiptVO, BigDecimal> total;
    public static volatile SingularAttribute<ReceiptVO, ReceiptType> type;
    public static volatile SingularAttribute<ReceiptVO, String> title;
    public static volatile SingularAttribute<ReceiptVO, Timestamp> applyTime;
    public static volatile SingularAttribute<ReceiptVO, ReceiptState> state;
    public static volatile SingularAttribute<ReceiptVO, String> receiptInfoUuid;
    public static volatile SingularAttribute<ReceiptVO, String> receiptAddressUuid;
    public static volatile SingularAttribute<ReceiptVO, String> receiptNumber;
    public static volatile SingularAttribute<ReceiptVO, String> comment;
    public static volatile SingularAttribute<ReceiptVO, Timestamp> createDate;
    public static volatile SingularAttribute<ReceiptVO, Timestamp> lastOpDate;
}
