package com.syscxp.alarm.header.contact;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactNotifyWayRefVO.class)
public class ContactNotifyWayRefVO_ {

    public static volatile SingularAttribute<ContactVO, String> contactUuid;
    public static volatile SingularAttribute<ContactVO, String> notifyWayUuid;
}
