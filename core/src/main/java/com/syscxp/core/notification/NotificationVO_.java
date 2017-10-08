package com.syscxp.core.notification;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by xing5 on 2017/3/15.
 */
@StaticMetamodel(NotificationVO.class)
public class NotificationVO_ {
    public static volatile SingularAttribute<NotificationVO, String> uuid;
    public static volatile SingularAttribute<NotificationVO, String> accountUuid;
    public static volatile SingularAttribute<NotificationVO, String> userUuid;
    public static volatile SingularAttribute<NotificationVO, String> name;
    public static volatile SingularAttribute<NotificationVO, String> sender;
    public static volatile SingularAttribute<NotificationVO, String> remoteIp;
    public static volatile SingularAttribute<NotificationVO, String> category;
    public static volatile SingularAttribute<NotificationVO, Boolean> success;
    public static volatile SingularAttribute<NotificationVO, Long> time;
    public static volatile SingularAttribute<NotificationVO, String> content;
    public static volatile SingularAttribute<NotificationVO, NotificationStatus> status;
    public static volatile SingularAttribute<NotificationVO, String> resourceUuid;
    public static volatile SingularAttribute<NotificationVO, String> resourceType;
    public static volatile SingularAttribute<NotificationVO, NotificationType> type;
    public static volatile SingularAttribute<NotificationVO, Timestamp> createDate;
    public static volatile SingularAttribute<NotificationVO, Timestamp> lastOpDate;
}
