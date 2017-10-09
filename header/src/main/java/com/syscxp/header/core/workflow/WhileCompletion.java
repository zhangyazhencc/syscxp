package com.syscxp.header.core.workflow;

import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.core.NoErrorCompletion;

/**
 * Created by Administrator on 2017-05-12.
 */
public abstract class WhileCompletion extends NoErrorCompletion {
    public WhileCompletion(AsyncBackup... completion) {
        super(completion);
    }
    public abstract void allDone();
}
