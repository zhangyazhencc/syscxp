package com.syscxp.core.externalusage;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ResourceExternalUsageVO.class)
public class ResourceExternalUsageVO_ {
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> uuid;
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> resourceUuid;
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> resourceType;
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> usedFor;
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> usedForResourceUuid;
    public static volatile SingularAttribute<ResourceExternalUsageVO, String> usedForResourceType;
    public static volatile SingularAttribute<ResourceExternalUsageVO, DeleteCascadeType> deleteCascadeType;
    public static volatile SingularAttribute<ResourceExternalUsageVO, Timestamp> createDate;
}
