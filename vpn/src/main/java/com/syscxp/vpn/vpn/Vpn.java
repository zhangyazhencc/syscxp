package com.syscxp.vpn.vpn;

import com.syscxp.header.message.Message;

public interface Vpn {
    void handleMessage(Message msg);
}
