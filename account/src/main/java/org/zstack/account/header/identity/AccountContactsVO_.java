package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.CompanyNature;
import org.zstack.header.identity.NoticeWay;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountContactsVO.class)
public class AccountContactsVO_ {
    public static volatile SingularAttribute<AccountExtraInfoVO, String> uuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> accountUuid;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> contacts;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> phone;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> email;
    public static volatile SingularAttribute<AccountExtraInfoVO, String> description;
    public static volatile SingularAttribute<AccountExtraInfoVO, NoticeWay> noticeWay;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountExtraInfoVO, Timestamp> lastOpDate;
}
