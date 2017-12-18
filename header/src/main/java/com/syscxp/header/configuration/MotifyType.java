package com.syscxp.header.configuration;

import com.syscxp.header.billing.OrderType;

/**
 * Create by DCY on 2017/9/30
 */
public enum MotifyType {
    UPGRADE,
    DOWNGRADE,
    Unknown;

    public static MotifyType valueOf(OrderType type) {
        if (type == OrderType.DOWNGRADE) {
            return DOWNGRADE;
        } else if (type == OrderType.UPGRADE) {
            return UPGRADE;
        } else {
            return Unknown;
        }
    }
}
