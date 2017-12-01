package com.syscxp.account.header.ticket;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/26.
 */

@StaticMetamodel(TicketVO.class)
public class TicketVO_ {
    public static volatile SingularAttribute<TicketVO, String> uuid;
    public static volatile SingularAttribute<TicketVO, String> accountUuid;
    public static volatile SingularAttribute<TicketVO, String> userUuid;
    public static volatile SingularAttribute<TicketVO, String> adminUserUuid;
    public static volatile SingularAttribute<TicketVO, String> ticketTypeUuid;
    public static volatile SingularAttribute<TicketVO, String> content;
    public static volatile SingularAttribute<TicketVO, String> contentExtra;
    public static volatile SingularAttribute<TicketVO, String> phone;
    public static volatile SingularAttribute<TicketVO, String> email;
    public static volatile SingularAttribute<TicketVO, TicketStatus> status;
    public static volatile SingularAttribute<TicketVO, TicketFrom> ticketFrom;
    public static volatile SingularAttribute<TicketVO, Timestamp> createDate;
    public static volatile SingularAttribute<TicketVO, Timestamp> lastOpDate;
}
