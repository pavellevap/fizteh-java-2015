package ru.fizteh.fivt.students.pavellevap.CQL;

import java.util.function.Function;

import ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl.Avg;
import ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl.Count;

class Aggregates {

    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        throw new UnsupportedOperationException();
    }

    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        throw new UnsupportedOperationException();
    }

    public static <C, T extends Comparable<T>> Function<C, T> count(Function<C, T> expression) {
        return new Count(expression);
    }

    public static <C, T extends Comparable<T>> Function<C, T> avg(Function<C, T> expression) {
        return new Avg(expression);
    }

}
