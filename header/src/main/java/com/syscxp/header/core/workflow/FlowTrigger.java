package com.syscxp.header.core.workflow;

import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.errorcode.ErrorCode;

/**
 */
public interface FlowTrigger extends AsyncBackup {
    void fail(ErrorCode errorCode);

    void next();

    void setError(ErrorCode error);

}
