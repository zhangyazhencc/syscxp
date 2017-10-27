package com.syscxp.billing.order;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(NotifyOrderVO.class)
public class NotifyOrderVO_ {
    public static volatile SingularAttribute<NotifyOrderVO, String> uuid;
    public static volatile SingularAttribute<NotifyOrderVO, String> orderUuid;
    public static volatile SingularAttribute<NotifyOrderVO, Integer> notifyTimes;
    public static volatile SingularAttribute<NotifyOrderVO, String> url;
    public static volatile SingularAttribute<NotifyOrderVO, String> accountUuid;
    public static volatile SingularAttribute<NotifyOrderVO, String> productUuid;
    public static volatile SingularAttribute<NotifyOrderVO, NotifyOrderStatus> status;
    public static volatile SingularAttribute<NotifyOrderVO, Timestamp> createDate;
    public static volatile SingularAttribute<NotifyOrderVO, Timestamp> lastOpDate;
}
