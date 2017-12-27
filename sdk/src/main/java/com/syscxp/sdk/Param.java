package com.syscxp.sdk;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:02
 * Author: wj
 */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Param {
    boolean required() default true;

    String[] validValues() default {};

    String validRegexValues() default "";

    int maxLength() default Integer.MIN_VALUE;

    int minLength() default Integer.MIN_VALUE;

    boolean nonempty() default false;

    boolean nullElements() default false;

    boolean emptyString() default true;

    long[] numberRange() default {};

    String[] numberRangeUnit() default {};

    boolean noTrim() default false;
}
