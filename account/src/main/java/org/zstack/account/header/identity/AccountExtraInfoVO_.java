package org.zstack.account.header.identity;

import org.zstack.header.identity.*;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountExtraInfoVO.class)
public class AccountExtraInfoVO_ {
    public static volatile SingularAttribute<AccountExtraInfoVO, String> uuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> accountUuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, AccountGrade> grade;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> specialLine;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> internetCloud;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> salesman;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> lastOpDate;
}
