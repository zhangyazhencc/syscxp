package org.zstack.account.header.ticket;

/**
 * Created by wangwg on 2017/9/25.
 */
public enum TicketStatus {
    /**
     * 未处理，处理中，待补充， 延迟处理， 已处理， 已解决
     *
     */
    untreated,
    inprocess,
    supplemented,
    delay,
    processed,
    resolved

}
