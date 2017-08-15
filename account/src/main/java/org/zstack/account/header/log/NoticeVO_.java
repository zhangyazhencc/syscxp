package org.zstack.account.header.log;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(NoticeVO.class)
public class NoticeVO_ {
    public static volatile SingularAttribute<OperLogVO, String> uuid;
    public static volatile SingularAttribute<OperLogVO, String> title;
    public static volatile SingularAttribute<OperLogVO, String> link;
    public static volatile SingularAttribute<OperLogVO, NoticeStatus> status;
    public static volatile SingularAttribute<OperLogVO, Timestamp> startTime;
    public static volatile SingularAttribute<OperLogVO, Timestamp> endTime;
    public static volatile SingularAttribute<OperLogVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<OperLogVO, Timestamp> createDate;
}
