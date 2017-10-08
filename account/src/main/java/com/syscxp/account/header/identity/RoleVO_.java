package com.syscxp.account.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(RoleVO.class)
public class RoleVO_ {
    public static volatile SingularAttribute<RoleVO, String> uuid;
    public static volatile SingularAttribute<RoleVO, String> name;

    public static volatile SingularAttribute<RoleVO, Timestamp> createDate;
    public static volatile SingularAttribute<RoleVO, String> accountUuid;
    public static volatile SingularAttribute<RoleVO, String> description;
    public static volatile SingularAttribute<RoleVO, Timestamp> lastOpDate;
}
