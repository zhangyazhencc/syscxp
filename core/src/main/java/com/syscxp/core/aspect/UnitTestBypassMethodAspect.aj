package com.syscxp.core.aspect;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

/**
 */
public aspect UnitTestBypassMethodAspect {
    private static final CLogger logger = Utils.getLogger(UnitTestBypassMethodAspect.class);

    Object around() : execution(@com.syscxp.header.core.BypassWhenUnitTest * *.*(..)) {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            logger.debug(String.format("bypass %s because of unit test", thisJoinPoint.getSignature().toLongString()));
            return null;
        } else {
            return proceed();
        }
    }
}
