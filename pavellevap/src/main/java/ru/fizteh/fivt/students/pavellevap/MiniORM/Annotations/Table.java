package ru.fizteh.fivt.students.pavellevap.MiniORM.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}

