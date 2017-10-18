package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactNotifyWayRefVO.class)
public class ContactNotifyWayRefVO_  extends BaseVO_ {

    public static volatile SingularAttribute<ContactVO, String> contactUuid;
    public static volatile SingularAttribute<ContactVO, String> notifyWayUuid;
}
