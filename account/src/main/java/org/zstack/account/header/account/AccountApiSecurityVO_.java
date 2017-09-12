package org.zstack.account.header.account;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountApiSecurityVO.class)
public class AccountApiSecurityVO_ {
    public static volatile SingularAttribute<AccountApiSecurityVO, String> uuid;
    public static volatile SingularAttribute<AccountApiSecurityVO, String> accountUuid;
    public static volatile SingularAttribute<AccountApiSecurityVO, String> publicKey;
    public static volatile SingularAttribute<AccountApiSecurityVO, String> privateKey;
    public static volatile SingularAttribute<AccountApiSecurityVO, String> allowIp;
    public static volatile SingularAttribute<AccountApiSecurityVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountApiSecurityVO, Timestamp> lastOpDate;
}
