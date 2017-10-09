package com.syscxp.core.aspect;

import com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException;
import com.syscxp.core.db.DatabaseGlobalProperty;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import java.util.concurrent.TimeUnit;

/**
 */
public aspect DbDeadlockAspect {
    private static final CLogger logger = Utils.getLogger(DbDeadlockAspect.class);

    declare error: withincode(@org.springframework.transaction.annotation.Transactional * *.*(..)) && withincode(@com.syscxp.core.db.DeadlockAutoRestart * *.*(..)) : "@Transactional and @DeadlockAutoRestart can not be present on the same method. @DeadlockAutoRestart must be on parent method which calls method that has @Transactional";

    Object around() : execution(@com.syscxp.core.db.DeadlockAutoRestart * *.*(..)) {
        RuntimeException bad = null;
        int times = DatabaseGlobalProperty.retryTimes;
        do {
            try {
                return proceed();
            } catch (RuntimeException re) {
                times --;
                bad = re;
                Throwable root = DebugUtils.getRootCause(re);
                if (root instanceof MySQLTransactionRollbackException && root.getMessage().contains("Deadlock")) {
                    logger.warn("deadlock happened, retry");

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.warn(e.getMessage(), e);
                    }
                } else {
                    throw re;
                }
            }
        } while (times > 0);

        logger.warn(String.format("DB deadlock still happens after retrying %s times, give up", DatabaseGlobalProperty.retryTimes));
        throw bad;
    }
}
