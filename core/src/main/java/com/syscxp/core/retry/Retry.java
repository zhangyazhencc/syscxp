package com.syscxp.core.retry;

import com.syscxp.core.Platform;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.TypeUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by xing5 on 2016/6/25.
 */
public abstract class Retry<T> {
    private static final CLogger logger = Utils.getLogger(Retry.class);

    protected abstract T call();

    protected int times = 5;

    protected String __name__;


    public Retry<T> setRetryTimes(int times){
        this.times = times;
        return this;
    }

    public T run() {
        Method m;
        try {
            m = getClass().getDeclaredMethod("call");
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }

        int interval = 1;
        Class[] onExceptions = {};

        RetryCondition cond = m.getAnnotation(RetryCondition.class);
        if (cond != null) {
            times = cond.times();
            interval = cond.interval();
            onExceptions = cond.onExceptions();
        }

        int count = times;

        if (__name__ == null) {
            __name__ = getClass().getName();
        }

        do {
            try {
                return call();
            } catch (Throwable t) {
                if (onExceptions.length != 0 && !TypeUtils.isTypeOf(t, onExceptions)) {
                    throw t;
                }

                try {
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }


                logger.debug(String.format("running [%s] encounters an exception[%s], will retry %s times with the" +
                        " interval[%s]", __name__, t.getMessage(), count, interval));

                count --;

                if (count == 0) {
                    ErrorCode errorCode = new ErrorCode();
                    errorCode.setCode(SysErrors.OPERATION_ERROR.toString());
                    errorCode.setDescription(Platform.i18n("an operation[%s] fails after retrying %s times with the interval %s seconds",
                            __name__, times, interval));
                    errorCode.setDetails(t.getMessage());
                    logger.warn(errorCode.toString(), t);

                    throw t;
                }
            }
        } while (true);
    }
}
