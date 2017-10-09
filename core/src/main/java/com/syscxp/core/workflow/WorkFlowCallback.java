package com.syscxp.core.workflow;

import com.syscxp.header.errorcode.ErrorCode;

public interface WorkFlowCallback {
    void succeed(WorkFlowContext ctx);
    
    void fail(WorkFlowContext ctx, ErrorCode error);
}
