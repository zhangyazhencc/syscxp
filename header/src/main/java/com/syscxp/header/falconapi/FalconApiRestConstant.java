package com.syscxp.header.falconapi;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-02.
 *
 */
public interface FalconApiRestConstant {
    // 同步监控通道ICMP数据
    static final String ICMP_SYNC = "/monitoring/icmp/save";

    // 删除监控通道ICMP数据
    static final String ICMP_DELETE = "/monitoring/icmp/delete";

    // 同步策略
    static final String STRATEGY_SYNC = "/monitoring/strategy/save";

    // 删除策略
    static final String STRATEGY_DELETE = "/monitoring/strategy/delete";
}
