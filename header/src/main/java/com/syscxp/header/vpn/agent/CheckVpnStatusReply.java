package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

import java.util.Map;

public class CheckVpnStatusReply extends MessageReply {
    private Map<String, String> states;

    public Map<String, String> getStates() {
        return states;
    }

    public void setStates(Map<String, String> states) {
        this.states = states;
    }
}
