package com.syscxp.alarm.header.log;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-12.
 * @Description: .
 */

public class HandleAlarmMsg extends NeedReplyMessage {
    private String alarmValue;

    public String getAlarmValue() {
        return alarmValue;
    }

    public void setAlarmValue(String alarmValue) {
        this.alarmValue = alarmValue;
    }
}
