package org.zstack.sms.header;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.Date;

@StaticMetamodel(SmsVO.class)
public class SmsVO_ {
    public static volatile SingularAttribute<SmsVO, Long> id;
    public static volatile SingularAttribute<SmsVO, String> accountUuid;
    public static volatile SingularAttribute<SmsVO, String> ip;
    public static volatile SingularAttribute<SmsVO, String> phone;
    public static volatile SingularAttribute<SmsVO, String> appId;
    public static volatile SingularAttribute<SmsVO, String> templateId;
    public static volatile SingularAttribute<SmsVO, String> data;
    public static volatile SingularAttribute<SmsVO, String> statusCode;
    public static volatile SingularAttribute<SmsVO, String> statusMsg;
    public static volatile SingularAttribute<SmsVO, String> dateCreated;
    public static volatile SingularAttribute<SmsVO, String> smsMessagesId;
    public static volatile SingularAttribute<SmsVO, String> msgEntrance;
    public static volatile SingularAttribute<SmsVO, Date> createDay;
    public static volatile SingularAttribute<SmsVO, Timestamp> createDate;
    public static volatile SingularAttribute<SmsVO, Timestamp> lastOpDate;
}
