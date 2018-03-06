package com.syscxp.account.header.identity;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.PolicyType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_ {
    public static volatile SingularAttribute<PolicyVO, String> uuid;
    public static volatile SingularAttribute<PolicyVO, String> name;
    public static volatile SingularAttribute<PolicyVO, String> permission;
    public static volatile SingularAttribute<PolicyVO, PolicyType> type;
    public static volatile SingularAttribute<PolicyVO, Integer> sortId;
    public static volatile SingularAttribute<PolicyVO, AccountType> accountType;
    public static volatile SingularAttribute<PolicyVO, String> description;
    public static volatile SingularAttribute<PolicyVO, Timestamp> createDate;
    public static volatile SingularAttribute<PolicyVO, Timestamp> lastOpDate;

}
