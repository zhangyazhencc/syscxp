package com.syscxp.core.workflow;

public interface AsyncWorkFlow {
    void process(WorkFlowContext ctx, AsyncWorkFlowChain chain) throws WorkFlowException;
    
    void rollback(WorkFlowContext ctx);
    
    String getName();
}
