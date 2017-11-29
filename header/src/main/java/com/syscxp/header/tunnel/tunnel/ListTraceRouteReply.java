package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.MessageReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
public class ListTraceRouteReply extends MessageReply {

    private List<List<String>> results;

    public List<List<String>> getResults() {
        return results;
    }

    public void setResults(List<List<String>> results) {
        this.results = results;
    }
}
