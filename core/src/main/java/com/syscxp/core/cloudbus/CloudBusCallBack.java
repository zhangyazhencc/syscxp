package com.syscxp.core.cloudbus;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.message.MessageReply;

public abstract class CloudBusCallBack extends AbstractCompletion {
    public CloudBusCallBack(AsyncBackup one, AsyncBackup...others) {
        super(one, others);
    }

	public abstract void run(MessageReply reply);
}
