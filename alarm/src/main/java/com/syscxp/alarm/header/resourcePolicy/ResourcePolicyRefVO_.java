package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ResourcePolicyRefVO.class)
public class ResourcePolicyRefVO_ {

    public static volatile SingularAttribute<ResourcePolicyRefVO, String> resourceUuid;
    public static volatile SingularAttribute<ResourcePolicyRefVO, Long> id;
    public static volatile SingularAttribute<ResourcePolicyRefVO, String> policyUuid;
    public static volatile SingularAttribute<BaseVO, Timestamp> createDate;
    public static volatile SingularAttribute<BaseVO, Timestamp> lastOpDate;
}
