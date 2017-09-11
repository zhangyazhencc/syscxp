package org.zstack.account.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountExtraInfoVO.class)
public class AccountExtraInfoVO_ {
    public static volatile SingularAttribute<AccountExtraInfoVO, String> uuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, AccountGrade> grade;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> userUuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> createWay;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> lastOpDate;
}
