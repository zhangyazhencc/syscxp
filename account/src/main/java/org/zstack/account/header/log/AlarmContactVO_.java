package org.zstack.account.header.log;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.List;

@StaticMetamodel(AlarmContactVO.class)
public class AlarmContactVO_ {
    public static volatile SingularAttribute<OperLogVO, String> uuid;
    public static volatile SingularAttribute<OperLogVO, String> name;
    public static volatile SingularAttribute<OperLogVO, String> phone;
    public static volatile SingularAttribute<OperLogVO, String> email;
    public static volatile SingularAttribute<OperLogVO, List<AlarmChannel>> channel;
    public static volatile SingularAttribute<OperLogVO, String> accountName;
    public static volatile SingularAttribute<OperLogVO, String> company;
    public static volatile SingularAttribute<OperLogVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<OperLogVO, Timestamp> createDate;
}
