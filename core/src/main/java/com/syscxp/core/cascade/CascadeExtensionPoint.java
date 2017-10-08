package com.syscxp.core.cascade;

import com.syscxp.header.core.Completion;

import java.util.List;

/**
 */
public interface CascadeExtensionPoint {
    void syncCascade(CascadeAction action) throws CascadeException;

    void asyncCascade(CascadeAction action, Completion completion);

    List<String> getEdgeNames();

    String getCascadeResourceName();

    CascadeAction createActionForChildResource(CascadeAction action);
}
