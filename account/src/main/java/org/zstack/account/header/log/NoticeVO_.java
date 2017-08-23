package org.zstack.account.header.log;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(NoticeVO.class)
public class NoticeVO_ {
    public static volatile SingularAttribute<NoticeVO, String> uuid;
    public static volatile SingularAttribute<NoticeVO, String> title;
    public static volatile SingularAttribute<NoticeVO, String> link;
    public static volatile SingularAttribute<NoticeVO, NoticeStatus> status;
    public static volatile SingularAttribute<NoticeVO, Timestamp> startTime;
    public static volatile SingularAttribute<NoticeVO, Timestamp> endTime;
    public static volatile SingularAttribute<NoticeVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<NoticeVO, Timestamp> createDate;
}
