package com.syscxp.header.message;

import java.util.List;

public interface MessageHandlerExtensionPoint<T extends Object> {
    List<String> getMessageNameTheExtensionServed();

    void handleMessage(Message msg, T context);
}
