package com.syscxp.header.identity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by frank on 7/9/2015.
 */
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Action {
    boolean adminOnly() default false;      //只SystemAdmin
    boolean accountOnly() default false;    //只帐户

    String product() default "";
    String category();

    String[] names() default {};

    boolean accountControl() default false;
}
