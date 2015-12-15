package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.function.Function;
import java.util.stream.StreamSupport;

public class Count<T> implements Aggregator<T, Long> {
    private Function<T, ?> function;
    public Count(Function<T, ?> function) {
        this.function = function;
    }

    public Long apply(Iterable<T> elements) {
        return StreamSupport.stream(elements.spliterator(), false).map(function).count();
    }

    public Long apply(T t) {
        return null;
    }
}
