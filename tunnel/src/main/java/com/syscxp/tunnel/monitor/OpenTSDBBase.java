package com.syscxp.tunnel.monitor;

import com.syscxp.header.tunnel.monitor.OpenTSDBCommands;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public interface OpenTSDBBase {
    List<OpenTSDBCommands.QueryResult> httpCall(OpenTSDBCommands.TunnelQueryCondition condition);
}
