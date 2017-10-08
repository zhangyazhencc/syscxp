package com.syscxp.core;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.SyncTaskChain;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.core.workflow.FlowTrigger;
import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

/**
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class With {
    private static final CLogger logger = Utils.getLogger(With.class);
    private Message[] msgs;
    private AsyncBackup backup;
    
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    public With(AsyncBackup backup, Message...msgs) {
        this.backup = backup;
        this.msgs = msgs;
        check();
    }

    public With(AsyncBackup backup) {
        this.backup = backup;
        check();
    }

    public With(Message...msgs) {
        this.msgs = msgs;
    }

    private void backup(Throwable t) {
        if (!(backup instanceof Message)) {
            logger.warn(String.format("unhandled exception happened"), t);
        }

        ErrorCode err = errf.throwableToInternalError(t);
        if (backup instanceof Completion) {
            ((Completion)backup).fail(err);
        } else if (backup instanceof ReturnValueCompletion) {
            ((ReturnValueCompletion)backup).fail(err);
        } else if (backup instanceof FlowTrigger) {
            ((FlowTrigger) backup).fail(err);
        } else if (backup instanceof SyncTaskChain) {
            ((SyncTaskChain) backup).next();
        } else if (backup instanceof NoErrorCompletion) {
            ((NoErrorCompletion) backup).done();
        } else if (backup instanceof Message) {
            bus.logExceptionWithMessageDump((Message) backup, t);
            bus.replyErrorByMessageType((Message) backup, err);
        } else {
            throw new CloudRuntimeException("should not be here");
        }

        if (msgs != null) {
            for (Message msg : msgs) {
                bus.logExceptionWithMessageDump(msg, t);
                bus.replyErrorByMessageType(msg, err);
            }
        }
    }

    private void check() {
        if (backup == null) {
            return;
        }

        if (!(
                backup instanceof Completion || backup instanceof ReturnValueCompletion || backup instanceof Message
                        || backup instanceof FlowTrigger || backup instanceof SyncTaskChain || backup instanceof NoErrorCompletion
        )) {
            String info = String.format("%s is not a known AsyncBackup. Someone added this type but forgot changing AsyncBackupAspect, please file bug", backup.getClass().getName());
            throw new CloudRuntimeException(info);
        }

    }

    public void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            backup(t);
        }
    }

    public void runWithException(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException re) {
            backup(re);
            throw re;
        } catch (Error err) {
            backup(err);
            throw err;
        }
    }
}
