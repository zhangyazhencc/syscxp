package com.syscxp.header.host;

import com.syscxp.header.message.Message;

public interface Host {
    void handleMessage(Message msg);
}
