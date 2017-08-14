package org.zstack.billing.header.identity.receipt;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ReceiptPostAddressVO.class)
public class ReceiptPostAddressVO_ {

    public static volatile SingularAttribute<ReceiptPostAddressVO, String> uuid;
    public static volatile SingularAttribute<ReceiptPostAddressVO, String> accountUuid;
    public static volatile SingularAttribute<ReceiptPostAddressVO, String> name;
    public static volatile SingularAttribute<ReceiptPostAddressVO, String> telephone;
    public static volatile SingularAttribute<ReceiptPostAddressVO, String> address;
    public static volatile SingularAttribute<ReceiptPostAddressVO, Boolean> isDefault;
    public static volatile SingularAttribute<ReceiptVO, Timestamp> createDate;
    public static volatile SingularAttribute<ReceiptVO, Timestamp> lastOpDate;

}
