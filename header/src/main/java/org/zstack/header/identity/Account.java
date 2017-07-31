package org.zstack.header.identity;

import org.zstack.header.message.Message;

public interface Account {
    void handleMessage(Message msg);
}
