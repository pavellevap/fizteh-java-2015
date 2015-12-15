package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {
    private Function<T, R> function;
    public Min(Function<T, R> function) {
        this.function = function;
    }

    public R apply(Iterable<T> elements) {
        List<T> list = StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
        T result = list.get(0);
        for (T element : list) {
            if (function.apply(element).compareTo(function.apply(result)) < 0) {
                result = element;
            }
        }
        return function.apply(result);
    }


    public R apply(T t) {
        return null;
    }
}
