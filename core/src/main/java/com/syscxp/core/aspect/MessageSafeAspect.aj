package com.syscxp.core.aspect;

import com.syscxp.core.errorcode.ErrorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.Message;

/**
 */
public aspect MessageSafeAspect {
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    after() throwing(Throwable t) : execution(@com.syscxp.core.cloudbus.MessageSafe * *.*(.., Message+, ..)) {
        for (Object arg : thisJoinPoint.getArgs()) {
            if (arg instanceof Message) {
                ErrorCode err = null;
                if (t instanceof OperationFailureException) {
                    err = ((OperationFailureException)t).getErrorCode();
                } else {
                    err = errf.throwableToInternalError(t);
                }

                bus.logExceptionWithMessageDump((Message)arg, t);
                bus.replyErrorByMessageType((Message)arg, err);
            }
        }
    }
}
