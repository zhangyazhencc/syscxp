package com.syscxp.header.vo;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ResourceVO.class)
public class ResourceVO_ {
    public static volatile SingularAttribute<ResourceVO, String> uuid;
    public static volatile SingularAttribute<ResourceVO, String> resourceName;
    public static volatile SingularAttribute<ResourceVO, String> resourceType;
}
