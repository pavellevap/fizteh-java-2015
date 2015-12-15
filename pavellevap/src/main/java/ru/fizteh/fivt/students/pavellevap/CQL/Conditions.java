package ru.fizteh.fivt.students.pavellevap.CQL;

import java.util.function.Function;
import java.util.function.Predicate;

public class Conditions<T> {

    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        return element -> expression.apply(element).matches(regexp);
    }

    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
        return element -> pattern.equals(expression.apply(element));
    }

}
