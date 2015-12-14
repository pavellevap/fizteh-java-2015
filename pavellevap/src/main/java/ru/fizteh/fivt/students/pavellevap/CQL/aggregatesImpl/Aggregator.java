package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.function.Function;

public interface Aggregator<T, R> extends Function<T, R> {
    public R apply(Iterable<T> iterable);
}