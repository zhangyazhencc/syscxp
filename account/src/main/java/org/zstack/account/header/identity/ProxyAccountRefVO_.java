package org.zstack.account.header.identity;

import org.zstack.account.header.user.UserPolicyRefVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by frank on 7/9/2015.
 */
@StaticMetamodel(ProxyAccountRefVO.class)
public class ProxyAccountRefVO_ {
    public static volatile SingularAttribute<UserPolicyRefVO, Long> id;
    public static volatile SingularAttribute<UserPolicyRefVO, String> accountUuid;
    public static volatile SingularAttribute<UserPolicyRefVO, String> customerAcccountUuid;
    public static volatile SingularAttribute<UserPolicyRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<UserPolicyRefVO, Timestamp> lastOpDate;
}
