package com.syscxp.core.cloudbus;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.message.MessageReply;

import java.util.List;

public abstract class CloudBusListCallBack extends AbstractCompletion {
    public CloudBusListCallBack(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void run(List<MessageReply> replies);
}
