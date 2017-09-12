package org.zstack.account.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/09/07.
 */
@StaticMetamodel(RolePolicyRefVO.class)
public class RolePolicyRefVO_ {
    public static volatile SingularAttribute<RolePolicyRefVO, Long> id;
    public static volatile SingularAttribute<RolePolicyRefVO, String> policyUuid;
    public static volatile SingularAttribute<RolePolicyRefVO, String> userUuid;
    public static volatile SingularAttribute<RolePolicyRefVO, Timestamp> createDate;
}
