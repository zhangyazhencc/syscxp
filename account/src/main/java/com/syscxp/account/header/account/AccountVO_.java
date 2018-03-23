package com.syscxp.account.header.account;


import com.syscxp.header.identity.ValidateStatus;
import com.syscxp.header.identity.AccountStatus;
import com.syscxp.header.identity.AccountType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountVO.class)
public class AccountVO_ {
    public static volatile SingularAttribute<AccountVO, String> uuid;
    public static volatile SingularAttribute<AccountVO, String> name;
    public static volatile SingularAttribute<AccountVO, String> password;
    public static volatile SingularAttribute<AccountVO, String> email;
    public static volatile SingularAttribute<AccountVO, ValidateStatus> emailStatus;
    public static volatile SingularAttribute<AccountVO, String> phone;
    public static volatile SingularAttribute<AccountVO, ValidateStatus> phonestatus;
    public static volatile SingularAttribute<AccountVO, String> trueName;
    public static volatile SingularAttribute<AccountVO, String> company;
    public static volatile SingularAttribute<AccountVO, String> industry;
    public static volatile SingularAttribute<AccountVO, AccountStatus> status;
    public static volatile SingularAttribute<AccountVO, String> description;
    public static volatile SingularAttribute<AccountVO, Boolean> expiredClean;
    public static volatile SingularAttribute<AccountVO, AccountType> type;
    public static volatile SingularAttribute<AccountVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountVO, Timestamp> lastOpDate;
}
