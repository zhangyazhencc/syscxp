package com.syscxp.vpn.vpn;

import com.syscxp.header.message.Message;

public interface VpnManager {
    void handleMessage(Message msg);
}
