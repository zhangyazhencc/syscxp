package org.zstack.account.header;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AuthorityVO.class)
public class AuthorityVO_ {
    public static volatile SingularAttribute<PolicyVO, String> uuid;
    public static volatile SingularAttribute<PolicyVO, String> name;
    public static volatile SingularAttribute<PolicyVO, String> authority;
    public static volatile SingularAttribute<PolicyVO, String> description;
    public static volatile SingularAttribute<PolicyVO, Timestamp> createDate;
    public static volatile SingularAttribute<PolicyVO, Timestamp> lastOpDate;
}
