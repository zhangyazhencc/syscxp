package org.zstack.account.header.user;

import org.zstack.header.identity.ValidateStatus;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(UserVO.class)
public class UserVO_ {
    public static volatile SingularAttribute<UserVO, String> uuid;
    public static volatile SingularAttribute<UserVO, String> accountUuid;
    public static volatile SingularAttribute<UserVO, String> name;
    public static volatile SingularAttribute<UserVO, String> password;
    public static volatile SingularAttribute<UserVO, String> email;
    public static volatile SingularAttribute<UserVO, ValidateStatus> emailStatus;
    public static volatile SingularAttribute<UserVO, String> phone;
    public static volatile SingularAttribute<UserVO, ValidateStatus> phoneStatus;
    public static volatile SingularAttribute<UserVO, String> trueName;
    public static volatile SingularAttribute<UserVO, String> department;
    public static volatile SingularAttribute<UserVO, String> status;
    public static volatile SingularAttribute<UserVO, UserType> userType;
    public static volatile SingularAttribute<UserVO, Timestamp> createDate;
    public static volatile SingularAttribute<UserVO, String> description;
    public static volatile SingularAttribute<UserVO, Timestamp> lastOpDate;
}
