package org.zstack.account.header.ticket;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/26.
 */

@StaticMetamodel(TicketTypeVO.class)
public class TicketTypeVO_ {
    public static volatile SingularAttribute<TicketTypeVO, Long> id;
    public static volatile SingularAttribute<TicketTypeVO, String> code;
    public static volatile SingularAttribute<TicketTypeVO, String> name;
    public static volatile SingularAttribute<TicketTypeVO, String> category;
    public static volatile SingularAttribute<TicketTypeVO, Timestamp> createDate;
    public static volatile SingularAttribute<TicketTypeVO, Timestamp> lastOpDate;
}
