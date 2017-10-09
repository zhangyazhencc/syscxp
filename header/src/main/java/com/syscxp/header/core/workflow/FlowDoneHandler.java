package com.syscxp.header.core.workflow;

import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.core.AbstractCompletion;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FlowDoneHandler extends AbstractCompletion {
    public FlowDoneHandler(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void handle(Map data);
}
