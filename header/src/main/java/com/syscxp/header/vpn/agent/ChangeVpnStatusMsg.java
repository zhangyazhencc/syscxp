package com.syscxp.header.vpn.agent;


public class ChangeVpnStatusMsg extends VpnMessage {
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
