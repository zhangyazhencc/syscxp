package com.syscxp.core.aspect;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.header.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.core.workflow.FlowRollback;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.core.workflow.FlowTrigger;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.Message;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.List;

public aspect AsyncBackupAspect {
    private final CLogger logger = Utils.getLogger(AsyncBackupAspect.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    private boolean isAsyncBackup(Object backup) {
        return backup instanceof Message
                || backup instanceof Completion
                || backup instanceof ReturnValueCompletion
                || backup instanceof AsyncLatch
                || backup instanceof FlowTrigger
                || backup instanceof SyncTaskChain
                || backup instanceof NoErrorCompletion
                || backup instanceof FlowRollback
                || backup instanceof ChainTask;
    }


    private void backup(List<AsyncBackup> ancestors, Throwable t, boolean flipThrowable) {
        if (ancestors == null || ancestors.isEmpty()) {
            // throw original exception out if no backup specified
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            } else if (t instanceof Error) {
                throw (Error)t;
            } else {
                throw new CloudRuntimeException(t);
            }
        }

        for (AsyncBackup ancestor : ancestors) {
            if (!isAsyncBackup(ancestor)) {
                String info = String.format("%s is not a known AsyncBackup. Someone added this type but forgot changing AsyncBackupAspect, please file bug", ancestor.getClass().getName());
                DebugUtils.dumpStackTrace(info);
                return;
            }

            ErrorCode err = t instanceof OperationFailureException ? ((OperationFailureException) t).getErrorCode() : errf.throwableToInternalError(t);
            if (ancestor instanceof Completion) {
                ((Completion)ancestor).fail(err);
            } else if (ancestor instanceof ReturnValueCompletion) {
                ((ReturnValueCompletion)ancestor).fail(err);
            } else if (ancestor instanceof AsyncLatch) {
                ((AsyncLatch) ancestor).ack();
            } else if (ancestor instanceof FlowTrigger) {
                ((FlowTrigger) ancestor).fail(err);
            } else if (ancestor instanceof FlowRollback) {
                ((FlowRollback) ancestor).rollback();
            } else if (ancestor instanceof  SyncTaskChain) {
                ((SyncTaskChain) ancestor).next();
            } else if (ancestor instanceof NoErrorCompletion) {
                ((NoErrorCompletion) ancestor).done();
            } else if (ancestor instanceof Message) {
                bus.logExceptionWithMessageDump((Message) ancestor, t);
                bus.replyErrorByMessageType((Message) ancestor, err);
            } else {
                throw new CloudRuntimeException("should not be here");
            }
        }

        if (flipThrowable && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        } else {
            logger.warn("unhandled exception happened", t);
        }
    }

    private void backup(List<AsyncBackup> ancestors, Throwable t) {
        backup(ancestors, t, false);
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.Completion+.success()) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.Completion+.fail(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.ReturnValueCompletion+.success(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.ReturnValueCompletion+.fail(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.NoErrorCompletion+.done()) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.NopeCompletion+.success()) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.NopeCompletion+.fail(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.core.cloudbus.CloudBusCallBack+.run(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.core.cloudbus.CloudBusListCallBack+.run(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.core.thread.ChainTask+.run(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t, true);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.rest.AsyncRESTCallback+.timeout(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.rest.AsyncRESTCallback+.success(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.rest.AsyncRESTCallback+.fail(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.workflow.FlowDoneHandler+.handle(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.workflow.FlowErrorHandler+.handle(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }

    void around(AbstractCompletion completion) : this(completion) && execution(void com.syscxp.header.core.workflow.FlowFinallyHandler+.Finally(..)) {
        try {
            proceed(completion);
        } catch (Throwable  t) {
            backup(completion.getBackups(), t);
        }
    }
}
