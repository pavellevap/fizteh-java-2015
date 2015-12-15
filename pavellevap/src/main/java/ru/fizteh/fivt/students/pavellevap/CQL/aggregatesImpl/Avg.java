package ru.fizteh.fivt.students.pavellevap.CQL.aggregatesImpl;

import java.util.function.Function;

public class Avg<T> implements Aggregator<T, Double> {
    private Function<T, ? extends Number> function;
    public Avg(Function<T, ? extends Number> function) {
        this.function = function;
    }

    public Double apply(Iterable<T> elements) {
        Double result = 0.0;
        Integer count = 0;
        for (T element : elements) {
            result += function.apply(element).doubleValue();
            count++;
        }
        return result / count;
    }

    public Double apply(T t) {
        return null;
    }
}
