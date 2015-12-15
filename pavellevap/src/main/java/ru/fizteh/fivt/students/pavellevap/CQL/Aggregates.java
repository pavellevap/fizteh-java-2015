package ru.fizteh.fivt.students.pavellevap.CQL;

import java.util.function.Function;

import ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl.*;

@SuppressWarnings("unchecked")
public class Aggregates {

    public static <C, T extends Comparable<T>> Aggregator<C, T> max(Function<C, T> expression) {
        return new Max(expression);
    }

    public static <C, T extends Comparable<T>> Aggregator<C, T> min(Function<C, T> expression) {
        return new Min(expression);
    }

    public static <C, T extends Comparable<T>> Aggregator<C, Long> count(Function<C, T> expression) {
        return new Count(expression);
    }

    public static <C, T extends Comparable<T>> Aggregator<C, Double> avg(Function<C, T> expression) {
        return new Avg(expression);
    }
}
