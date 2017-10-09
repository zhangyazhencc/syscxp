package com.syscxp.core.aspect;

import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

public aspect ExceptionSafeAspect {
    private static final CLogger logger = Utils.getLogger(ExceptionSafeAspect.class);

    void around() : execution(@com.syscxp.header.core.ExceptionSafe * *.*(..)) {
        try {
            proceed();
        } catch (Throwable t) {
            logger.warn("unhandled exception happened", t);
        }
    }
}
