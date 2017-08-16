package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.AccountIndustry;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountVO.class)
public class AccountVO_ {
    public static volatile SingularAttribute<AccountVO, String> uuid;
    public static volatile SingularAttribute<AccountVO, String> name;
    public static volatile SingularAttribute<AccountVO, String> password;
    public static volatile SingularAttribute<AccountVO, String> email;
    public static volatile SingularAttribute<AccountVO, String> phone;
    public static volatile SingularAttribute<AccountVO, String> trueName;
    public static volatile SingularAttribute<AccountVO, String> company;
    public static volatile SingularAttribute<AccountVO, AccountIndustry> industry;
    public static volatile SingularAttribute<AccountVO, AccountStatus> status;
    public static volatile SingularAttribute<AccountVO, String> description;
    public static volatile SingularAttribute<AccountVO, AccountType> type;
    public static volatile SingularAttribute<AccountVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountVO, Timestamp> lastOpDate;
}
