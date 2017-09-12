package org.zstack.account.header.identity;

import org.zstack.account.header.user.UserRoleRefVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by frank on 7/9/2015.
 */
@StaticMetamodel(ProxyAccountRefVO.class)
public class ProxyAccountRefVO_ {
    public static volatile SingularAttribute<UserRoleRefVO, Long> id;
    public static volatile SingularAttribute<UserRoleRefVO, String> accountUuid;
    public static volatile SingularAttribute<UserRoleRefVO, String> customerAcccountUuid;
    public static volatile SingularAttribute<UserRoleRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<UserRoleRefVO, Timestamp> lastOpDate;
}
