package com.syscxp.header.identity;

import com.syscxp.header.message.Message;

public interface Account {
    void handleMessage(Message msg);
}
