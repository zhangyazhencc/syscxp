package com.syscxp.header.core;

import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

/**
 */
public class NopeCompletion extends Completion {
    private static final CLogger logger = Utils.getLogger(NopeCompletion.class);

    public NopeCompletion(AsyncBackup... others) {
        super(null, others);
    }


    @Override
    public void success() {
    }

    @Override
    public void fail(ErrorCode errorCode) {
        logger.warn(errorCode.toString());
    }
}
