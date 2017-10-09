package com.syscxp.header.core.workflow;

import com.syscxp.header.core.AsyncBackup;

/**
 * Created by frank on 11/20/2015.
 */
public interface FlowRollback extends AsyncBackup {
    void rollback();

    void skipRestRollbacks();
}
