package org.zstack.account.header.identity;

import org.zstack.header.identity.PermissionType;
import org.zstack.header.identity.PermissionVisible;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(PermissionVO.class)
public class PermissionVO_ {
    public static volatile SingularAttribute<PermissionVO, String> uuid;
    public static volatile SingularAttribute<PermissionVO, String> name;
    public static volatile SingularAttribute<PermissionVO, String> permission;
    public static volatile SingularAttribute<PermissionVO, PermissionType> type;
    public static volatile SingularAttribute<PermissionVO, Integer> sortId;
    public static volatile SingularAttribute<PermissionVO, PermissionVisible> visible;
    public static volatile SingularAttribute<PermissionVO, String> description;
    public static volatile SingularAttribute<PermissionVO, Timestamp> createDate;
    public static volatile SingularAttribute<PermissionVO, Timestamp> lastOpDate;

}
