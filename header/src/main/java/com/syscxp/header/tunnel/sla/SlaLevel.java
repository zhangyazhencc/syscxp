package com.syscxp.header.tunnel.sla;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-08.
 * @Description: SLA等级.
 */
public enum SlaLevel {
    A(1, 30),
    B(5, 20),
    C(10, 10),
    D(30, 5);

    /**
     * 带宽占用率
     */
    private long rate;

    /***
     * 持续时间
     */
    private long duration;

    SlaLevel(long rate, long duration) {
        this.rate = rate;
        this.duration = duration;
    }

    public long getRate() {
        return rate;
    }

    public long getDuration() {
        return duration;
    }
}
