package ru.fizteh.fivt.students.pavellevap.MiniORM;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
}
