package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.MessageReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
public class ListTraceRouteReply extends MessageReply {

    private List<List<String>> msg;

    public List<List<String>> getMsg() {
        return msg;
    }

    public void setMsg(List<List<String>> msg) {
        this.msg = msg;
    }
}
