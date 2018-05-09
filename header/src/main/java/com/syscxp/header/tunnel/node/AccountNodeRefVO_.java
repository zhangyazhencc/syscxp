package com.syscxp.header.tunnel.node;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/3
 */
@StaticMetamodel(AccountNodeRefVO.class)
public class AccountNodeRefVO_ {

    public static volatile SingularAttribute<AccountNodeRefVO, String> uuid;
    public static volatile SingularAttribute<AccountNodeRefVO, String> accountUuid;
    public static volatile SingularAttribute<AccountNodeRefVO, String> nodeUuid;
    public static volatile SingularAttribute<AccountNodeRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountNodeRefVO, Timestamp> lastOpDate;
}
