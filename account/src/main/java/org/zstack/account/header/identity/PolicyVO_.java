package org.zstack.account.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_ {
    public static volatile SingularAttribute<PolicyVO, String> uuid;
    public static volatile SingularAttribute<PolicyVO, String> name;
    public static volatile SingularAttribute<PolicyVO, String> policyStatement;
    public static volatile SingularAttribute<PolicyVO, Timestamp> createDate;
    public static volatile SingularAttribute<PolicyVO, String> accountUuid;
    public static volatile SingularAttribute<PolicyVO, String> description;
    public static volatile SingularAttribute<PolicyVO, Timestamp> lastOpDate;
}
