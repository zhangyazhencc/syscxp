package com.syscxp.account.header.log;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.List;

@StaticMetamodel(AlarmContactVO.class)
public class AlarmContactVO_ {
    public static volatile SingularAttribute<AlarmContactVO, String> uuid;
    public static volatile SingularAttribute<AlarmContactVO, String> name;
    public static volatile SingularAttribute<AlarmContactVO, String> phone;
    public static volatile SingularAttribute<AlarmContactVO, String> email;
    public static volatile SingularAttribute<AlarmContactVO, List<AlarmChannel>> channel;
    public static volatile SingularAttribute<AlarmContactVO, String> accountName;
    public static volatile SingularAttribute<AlarmContactVO, String> company;
    public static volatile SingularAttribute<AlarmContactVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<AlarmContactVO, Timestamp> createDate;
}
