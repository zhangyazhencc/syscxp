package org.zstack.account.header.identity;


import org.zstack.header.identity.NoticeWay;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/8/16.
 */

@StaticMetamodel(AccountContactsVO.class)
public class AccountContactsVO_ {
    public static volatile SingularAttribute<AccountContactsVO, String> uuid;
    public static volatile SingularAttribute<AccountContactsVO, String> accountUuid;
    public static volatile SingularAttribute<AccountContactsVO, String> name;
    public static volatile SingularAttribute<AccountContactsVO, String> phone;
    public static volatile SingularAttribute<AccountContactsVO, String> email;
    public static volatile SingularAttribute<AccountContactsVO, String> description;
    public static volatile SingularAttribute<AccountContactsVO, NoticeWay> noticeWay;
    public static volatile SingularAttribute<AccountContactsVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountContactsVO, Timestamp> lastOpDate;
}
