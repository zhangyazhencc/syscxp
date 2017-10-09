package com.syscxp.core.cloudbus;

import com.syscxp.header.message.Event;

public interface CloudBusEventListener {
    boolean handleEvent(Event e);
}
