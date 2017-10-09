package com.syscxp.header.core.workflow;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;

/**
 * Created by xing5 on 2016/3/29.
 */
public abstract class FlowFinallyHandler extends AbstractCompletion {
    public FlowFinallyHandler(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void Finally();
}
