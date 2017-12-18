package com.syscxp.header.configuration;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/10
 */
@StaticMetamodel(ResourceMotifyRecordVO.class)
public class ResourceMotifyRecordVO_ {
    public static volatile SingularAttribute<ResourceMotifyRecordVO, String> uuid;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, String> resourceUuid;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, String> resourceType;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, String> opAccountUuid;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, String> opUserUuid;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, MotifyType> motifyType;
    public static volatile SingularAttribute<ResourceMotifyRecordVO, Timestamp> createDate;
}
