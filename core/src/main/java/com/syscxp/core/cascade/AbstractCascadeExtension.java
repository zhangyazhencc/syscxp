package com.syscxp.core.cascade;

import com.syscxp.header.core.Completion;

/**
 */
public abstract class AbstractCascadeExtension implements CascadeExtensionPoint {
    @Override
    public void syncCascade(CascadeAction action) throws CascadeException {
    }

    @Override
    public void asyncCascade(CascadeAction action, Completion completion) {
        completion.success();
    }
}
