package com.syscxp.alarm.header.contact;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(NotifyWayVO.class)
public class NotifyWayVO_ {

    public static volatile SingularAttribute<ContactVO, String> name;
    public static volatile SingularAttribute<ContactVO, String> code;
}
