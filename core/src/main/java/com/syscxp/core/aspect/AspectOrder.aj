package com.syscxp.core.aspect;

/**
 */
public aspect AspectOrder {
    declare precedence : ExceptionSafeAspect, ThreadAspect, MessageSafeAspect, AsyncSafeAspect, AsyncBackupAspect,
            CompletionSingleCallAspect, EncryptAspect, DecryptAspect, DbDeadlockAspect, DeferAspect, AnnotationTransactionAspect, UnitTestBypassMethodAspect;
}
