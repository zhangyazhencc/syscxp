package com.syscxp.core.thread;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;

public abstract class ChainTask extends AbstractCompletion {
    public ChainTask(AsyncBackup one, AsyncBackup...others) {
        super(one, others);
    }

    public abstract String getSyncSignature();

    public abstract void run(SyncTaskChain chain);

    public abstract String getName();

    protected int getSyncLevel() {
        return 1;
    }
}
