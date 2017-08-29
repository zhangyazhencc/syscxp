package org.zstack.billing.header.identity.receipt;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ReceiptInfoVO.class)
public class ReceiptInfoVO_ {


    public static volatile SingularAttribute<ReceiptInfoVO, String> uuid;
    public static volatile SingularAttribute<ReceiptInfoVO, String> accountUuid;
    public static volatile SingularAttribute<ReceiptInfoVO, ReceiptType> type;
    public static volatile SingularAttribute<ReceiptInfoVO, String> title;
    public static volatile SingularAttribute<ReceiptInfoVO, String> bankName;
    public static volatile SingularAttribute<ReceiptInfoVO, String> bankAccountNumber;
    public static volatile SingularAttribute<ReceiptInfoVO, String> telephone;
    public static volatile SingularAttribute<ReceiptInfoVO, String> identifyNumber;
    public static volatile SingularAttribute<ReceiptInfoVO, String> address;
    public static volatile SingularAttribute<ReceiptInfoVO, Boolean> isDefault;
    public static volatile SingularAttribute<ReceiptInfoVO, Timestamp> createDate;
    public static volatile SingularAttribute<ReceiptInfoVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<ReceiptInfoVO, String> comment;
}
