package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactVO.class)
public class ContactVO_  extends BaseVO_ {
    public static volatile SingularAttribute<ContactVO, String> name;
    public static volatile SingularAttribute<ContactVO, String> email;
    public static volatile SingularAttribute<ContactVO, String> mobile;
}
