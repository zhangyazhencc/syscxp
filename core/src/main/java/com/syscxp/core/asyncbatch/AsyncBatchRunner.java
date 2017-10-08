package com.syscxp.core.asyncbatch;

import com.syscxp.header.core.NoErrorCompletion;

/**
 * Created by xing5 on 2016/6/26.
 */
public interface AsyncBatchRunner {
    void run(NoErrorCompletion completion);
}
