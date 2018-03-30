package com.syscxp.idc.header.trustee;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ResourceOrderEffectiveVO.class)
public class ResourceOrderEffectiveVO_ {

    public static volatile SingularAttribute<ResourceOrderEffectiveVO, String> uuid;
    public static volatile SingularAttribute<ResourceOrderEffectiveVO, String> resourceUuid;
    public static volatile SingularAttribute<ResourceOrderEffectiveVO, String> resourceType;
    public static volatile SingularAttribute<ResourceOrderEffectiveVO, String> orderUuid;
    public static volatile SingularAttribute<ResourceOrderEffectiveVO, Timestamp> createDate;
}
