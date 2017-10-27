package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactGroupVO.class)
public class ContactGroupVO_ extends BaseVO_ {
    public static volatile SingularAttribute<ContactVO, String> groupCode;
    public static volatile SingularAttribute<ContactVO, String> groupName;
    public static volatile SingularAttribute<ContactVO, String> accountUuid;
}
