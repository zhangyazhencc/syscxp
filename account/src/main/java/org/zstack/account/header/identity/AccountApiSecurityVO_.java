package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.CompanyNature;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountApiSecurityVO.class)
public class AccountApiSecurityVO_ {
    public static volatile SingularAttribute<AccountExtraInfoVO, String> uuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> accountUuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> publicKey;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> privateKey;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> allowIp;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> lastOpDate;
}
