package org.zstack.account.header.user;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by frank on 7/9/2015.
 */
@StaticMetamodel(UserRoleRefVO.class)
public class UserRoleRefVO_ {
    public static volatile SingularAttribute<UserRoleRefVO, Long> id;
    public static volatile SingularAttribute<UserRoleRefVO, String> roleUuid;
    public static volatile SingularAttribute<UserRoleRefVO, String> userUuid;
    public static volatile SingularAttribute<UserRoleRefVO, Timestamp> createDate;
}
