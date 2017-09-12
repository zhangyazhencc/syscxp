package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.PermissionType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_ {
    public static volatile SingularAttribute<PolicyVO, String> uuid;
    public static volatile SingularAttribute<PolicyVO, String> name;
    public static volatile SingularAttribute<PolicyVO, String> permission;
    public static volatile SingularAttribute<PolicyVO, PermissionType> type;
    public static volatile SingularAttribute<PolicyVO, Integer> sortId;
    public static volatile SingularAttribute<PolicyVO, AccountType> accountType;
    public static volatile SingularAttribute<PolicyVO, String> description;
    public static volatile SingularAttribute<PolicyVO, Timestamp> createDate;
    public static volatile SingularAttribute<PolicyVO, Timestamp> lastOpDate;

}
