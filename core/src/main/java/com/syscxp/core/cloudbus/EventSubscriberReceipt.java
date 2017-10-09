package com.syscxp.core.cloudbus;

import com.syscxp.header.message.Event;

/**
 */
public interface EventSubscriberReceipt {
    void unsubscribe(Event e);

    void unsubscribeAll();
}
