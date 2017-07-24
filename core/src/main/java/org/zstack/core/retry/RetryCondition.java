package org.zstack.core.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xing5 on 2016/6/25.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryCondition {
    Class[] onExceptions() default {};

    int times() default 5;

    int interval() default 1;
}
