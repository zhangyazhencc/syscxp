package com.syscxp.header.tunnel.sla;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-08.
 * @Description: SLA等级.
 */
public enum SlaLevel {
    A(1, 1800),
    B(5, 1200),
    C(10, 600),
    D(30, 300);

    /**
     * 带宽占用率(%)
     */
    private long rate;

    /***
     * 持续时间(s:秒)
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
