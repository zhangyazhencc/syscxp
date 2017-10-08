package com.syscxp.core.cloudbus;

import com.syscxp.header.message.Message;

import java.util.List;

/**
 */
public interface ReplyMessagePreSendingExtensionPoint {
    List<Class> getReplyMessageClassForPreSendingExtensionPoint();

    void marshalReplyMessageBeforeSending(Message msg);
}
