package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Count<T> implements Aggregator<T, Long> {
    private Function<T, ?> function;
    public Count(Function<T, ?> function) {
        this.function = function;
    }

    public Long apply(Iterable<T> elements) {
        Set<Object> distinctElements = new HashSet<>();
        for (T element : elements) {
            distinctElements.add(function.apply(element));
        }
        return (long) distinctElements.size();
    }

    public Long apply(T t) {
        return null;
    }
}
