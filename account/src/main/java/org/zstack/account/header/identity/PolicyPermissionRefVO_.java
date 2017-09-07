package org.zstack.account.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/09/07.
 */
@StaticMetamodel(PolicyPermissionRefVO.class)
public class PolicyPermissionRefVO_ {
    public static volatile SingularAttribute<PolicyPermissionRefVO, Long> id;
    public static volatile SingularAttribute<PolicyPermissionRefVO, String> policyUuid;
    public static volatile SingularAttribute<PolicyPermissionRefVO, String> userUuid;
    public static volatile SingularAttribute<PolicyPermissionRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<PolicyPermissionRefVO, Timestamp> lastOpDate;
}
