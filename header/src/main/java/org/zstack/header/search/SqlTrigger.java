package org.zstack.header.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlTrigger {
    Class<?> foreignVOClass() default Object.class;

    String foreignVOJoinColumn() default "";

    Class<?> foreignVOToDeleteClass() default Object.class;

    String foreignVOToDeleteJoinColumn() default "";
}
