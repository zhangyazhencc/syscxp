package com.syscxp.core.aspect;

import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.CloudBusListCallBack;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.NopeCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.core.workflow.FlowDoneHandler;
import com.syscxp.header.core.workflow.FlowErrorHandler;
import com.syscxp.header.core.workflow.FlowFinallyHandler;
import com.syscxp.header.rest.AsyncRESTCallback;
import com.syscxp.core.thread.Task;
import com.syscxp.header.HasThreadContext;
import org.apache.logging.log4j.ThreadContext;

public aspect SetThreadContextAspect {
    private void setThreadContext(Object obj) {
        HasThreadContext tc = (HasThreadContext)obj;
        if (tc.threadContext != null) {
           ThreadContext.putAll(tc.threadContext);
        } else {
            ThreadContext.clearMap();
        }
        if (tc.threadContextStack != null) {
            ThreadContext.clearStack();
            ThreadContext.setStack(tc.threadContextStack);
        } else {
            ThreadContext.clearStack();
        }
    }

    before(Task task) : target(task) && execution(* Task+.call()) {
        setThreadContext(task);
    }

    before(Completion c) : target(c) && execution(* com.syscxp.header.core.Completion+.success()) {
        setThreadContext(c);
    }

    before(Completion c) : target(c) && execution(* com.syscxp.header.core.Completion+.fail(..)) {
        setThreadContext(c);
    }

    before(ReturnValueCompletion c) : target(c) && execution(* com.syscxp.header.core.ReturnValueCompletion+.success(..)) {
        setThreadContext(c);
    }

    before(ReturnValueCompletion c) : target(c) && execution(* com.syscxp.header.core.ReturnValueCompletion+.fail(..)) {
        setThreadContext(c);
    }

    before(NoErrorCompletion c) : target(c) && execution(* com.syscxp.header.core.NoErrorCompletion+.done()) {
        setThreadContext(c);
    }

    before(NopeCompletion c) : target(c) && execution(* com.syscxp.header.core.NopeCompletion+.success()) {
        setThreadContext(c);
    }

    before(NopeCompletion c) : target(c) && execution(* com.syscxp.header.core.NopeCompletion+.fail(..)) {
        setThreadContext(c);
    }

    before(CloudBusCallBack c) : target(c) && execution(* com.syscxp.core.cloudbus.CloudBusCallBack+.run(..)) {
        setThreadContext(c);
    }

    before(CloudBusListCallBack c) : target(c) && execution(* com.syscxp.core.cloudbus.CloudBusListCallBack+.run(..)) {
        setThreadContext(c);
    }

    before(ChainTask c) : target(c) && execution(* com.syscxp.core.thread.ChainTask+.run(..)) {
        setThreadContext(c);
    }

    before(AsyncRESTCallback c) : target(c) && execution(* com.syscxp.header.rest.AsyncRESTCallback+.timeout(..)) {
        setThreadContext(c);
    }

    before(AsyncRESTCallback c) : target(c) && execution(* com.syscxp.header.rest.AsyncRESTCallback+.success(..)) {
        setThreadContext(c);
    }

    before(AsyncRESTCallback c) : target(c) && execution(* com.syscxp.header.rest.AsyncRESTCallback+.fail(..)) {
        setThreadContext(c);
    }

    before(FlowDoneHandler c) : target(c) && execution(* com.syscxp.header.core.workflow.FlowDoneHandler+.handle(..)) {
        setThreadContext(c);
    }

    before(FlowErrorHandler c) : target(c) && execution(* com.syscxp.header.core.workflow.FlowErrorHandler+.handle(..)) {
        setThreadContext(c);
    }

    before(FlowFinallyHandler c) : target(c) && execution(* com.syscxp.header.core.workflow.FlowFinallyHandler+.Finally(..)) {
        setThreadContext(c);
    }
}