package com.syscxp.core.validation;

import com.syscxp.header.errorcode.ErrorCode;

/**
 */
public interface ValidationFacade {
    void validateErrorByException(Object obj);

    ErrorCode validateErrorByErrorCode(Object obj);
}
