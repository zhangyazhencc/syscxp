package com.syscxp.account.header.identity;

import com.syscxp.header.identity.AccountType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SessionVO.class)
public class SessionVO_ {
    public static volatile SingularAttribute<SessionVO, String> uuid;
    public static volatile SingularAttribute<SessionVO, String> accountUuid;
    public static volatile SingularAttribute<SessionVO, String> userUuid;
    public static volatile SingularAttribute<SessionVO, AccountType> type;
    public static volatile SingularAttribute<SessionVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<SessionVO, Timestamp> createDate;
}
