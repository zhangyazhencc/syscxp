package org.zstack.account.header.ticket;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/26.
 */

@StaticMetamodel(TicketRecordVO.class)
public class TicketRecordVO_ {
    public static volatile SingularAttribute<TicketRecordVO, String> uuid;
    public static volatile SingularAttribute<TicketRecordVO, String> ticketUuid;
    public static volatile SingularAttribute<TicketRecordVO, String> belongTo;
    public static volatile SingularAttribute<TicketRecordVO, String> content;
    public static volatile SingularAttribute<TicketRecordVO, TicketStatus> status;
    public static volatile SingularAttribute<TicketRecordVO, Timestamp> createDate;
    public static volatile SingularAttribute<TicketRecordVO, Timestamp> lastOpDate;
}
