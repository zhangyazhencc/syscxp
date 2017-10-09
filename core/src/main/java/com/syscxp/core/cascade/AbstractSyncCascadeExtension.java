package com.syscxp.core.cascade;

import com.syscxp.header.core.Completion;

/**
 */
public abstract class AbstractSyncCascadeExtension implements CascadeExtensionPoint {
    @Override
    public void asyncCascade(CascadeAction action, Completion completion) {
        completion.success();
    }
}
