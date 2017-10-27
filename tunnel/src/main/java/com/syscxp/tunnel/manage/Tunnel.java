package com.syscxp.tunnel.manage;

import com.syscxp.header.message.Message;

/**
 * Create by DCY on 2017/10/26
 */
public interface Tunnel {
    void handleMessage(Message msg);
}
