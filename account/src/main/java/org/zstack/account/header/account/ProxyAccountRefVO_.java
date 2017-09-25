package org.zstack.account.header.account;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by frank on 7/9/2015.
 */
@StaticMetamodel(ProxyAccountRefVO.class)
public class ProxyAccountRefVO_ {
    public static volatile SingularAttribute<ProxyAccountRefVO, Long> id;
    public static volatile SingularAttribute<ProxyAccountRefVO, String> accountUuid;
    public static volatile SingularAttribute<ProxyAccountRefVO, String> customerAccountUuid;
    public static volatile SingularAttribute<ProxyAccountRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<ProxyAccountRefVO, Timestamp> lastOpDate;
}
