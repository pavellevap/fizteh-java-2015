package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Avg<T> implements Aggregator<T, Long> {
    private Function<T, ? extends Number> function;
    public Avg(Function<T, ? extends Number> function) {
        this.function = function;
    }

    public Long apply(Iterable<T> elements) {
        Long result = 0l;
        Integer count = 0;
        for (T element : elements) {
            result += (Long)function.apply(element);
            count++;
        }
        return result / count;
    }

    public Long apply(T t) { return null; }
}