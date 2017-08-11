package org.zstack.account.header.identity.VO;

import org.zstack.header.identity.AccountType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SessionVO.class)
public class SessionVO_ {
    public static volatile SingularAttribute<SessionVO, String> uuid;
    public static volatile SingularAttribute<SessionVO, String> accountUuid;
    public static volatile SingularAttribute<SessionVO, String> userUuid;
    public static volatile SingularAttribute<AccountVO, AccountType> type;
    public static volatile SingularAttribute<SessionVO, Timestamp> expiredDate;
    public static volatile SingularAttribute<SessionVO, Timestamp> createDate;
}
