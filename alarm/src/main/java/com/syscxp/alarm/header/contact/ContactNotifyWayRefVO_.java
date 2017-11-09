package com.syscxp.alarm.header.contact;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(ContactNotifyWayRefVO.class)
public class ContactNotifyWayRefVO_  {

    public static volatile SingularAttribute<BaseVO, Long> id;

    public static volatile SingularAttribute<ContactVO, String> contactUuid;
    public static volatile SingularAttribute<ContactVO, String> notifyWayUuid;
    public static volatile SingularAttribute<BaseVO, Timestamp> createDate;
    public static volatile SingularAttribute<BaseVO, Timestamp> lastOpDate;
}
