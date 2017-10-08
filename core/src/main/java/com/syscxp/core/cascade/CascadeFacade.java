package com.syscxp.core.cascade;

import com.syscxp.header.core.Completion;

/**
 */
public interface CascadeFacade {
    void syncCascade(String actionCode, String issuer, Object context) throws CascadeException;

    void asyncCascade(String actionCode, String issuer, Object context, Completion completion);

    void asyncCascadeFull(String actionCode, String issuer, Object context, Completion completion);

    void syncCascade(CascadeAction action) throws CascadeException;

    void asyncCascade(CascadeAction action, Completion completion);

    void syncCascadeNoException(String actionCode, String issuer, Object context);

    void syncCascadeNoException(CascadeAction action);
}
